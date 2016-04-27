package controllers

import java.time.LocalDateTime
import java.util.UUID

import auth.DefaultEnv
import auth.model.core.User
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import model.exchange.{Bad, CreatePassword, Good, SignUp}
import auth.persistence.model.dao.LoginInfoDao
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import auth.service.UserService
import com.google.inject.Inject
import model.core.UserToken
import model.core.UserToken.TokenAction
import service.UserTokenService

import scala.concurrent.Future

/**
  * Sign up user to the system
  * This is mostly related to interaction with silhouette
  */
class SignUpController @Inject() (silhouette: Silhouette[DefaultEnv],
    passwordHasher: PasswordHasher,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    authInfoRepository: AuthInfoRepository) extends Controller with ResponseHelpers {

  import formatting.exchange.rest._
  import play.api.libs.concurrent.Execution.Implicits._

  /**
    * Registers users in auth.service and issues token in backend
    */
  def signUpRequestRegistration: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[SignUp].map { signUp ⇒
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)

      userService.retrieve(loginInfo).flatMap {
        case Some(user) ⇒
          Future.successful(Conflict(Json.toJson(Bad("user.exists"))))

        case None ⇒
          val user = User(UUID.randomUUID.toString, signUp.identifier, signUp.firstName, signUp.lastName,
            User.State.Created)

          for {
            user ← userService.save(user)
            _ ← loginInfoDao.save(loginInfo, user.uuid) // todo: this should be in one transaction
            registrationToken ← userTokenService.issue(user.uuid, TokenAction.ActivateAccount) // TODO token type
          } yield {
            // TODO: remove token from here, do not return it, so users have to visit email - in email activate link is not link to api
            Created(Json.toJson(Good(registrationToken.token))) // TODO:set location
          }
      }
    }.recoverTotal(badRequestWithMessage)
  }

  /**
    * Creates a password for user that is found with `token` and is in correct state
    */
  def createPassword(token: String): Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[CreatePassword].map { requestPw ⇒
      userTokenService.claim(token).flatMap {
        // TODO token (activatetoken? newpassword?)
        case Some(UserToken(_, userUuid, expiresOn, UserToken.TokenAction.ActivateAccount)) if !isTokenExpired(expiresOn) ⇒

          // TODO return token as well
          for {
            _ ← userService.setState(userUuid, User.State.Activated)
            Some(user) ← userService.retrieve(userUuid) // todo: test what if none
            loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            authInfo = passwordHasher.hash(requestPw.password)
            _ ← authInfoRepository.add(loginInfo, authInfo)
          } yield {
            Ok(Json.toJson(Good.empty))
          }

        case _ ⇒ Future.successful(NotFound(Json.toJson(Bad("token.invalid"))))
      }
    }.recoverTotal(badRequestWithMessage)
  }

  private def isTokenExpired(expiresOn: LocalDateTime): Boolean = expiresOn.isBefore(LocalDateTime.now)
}
