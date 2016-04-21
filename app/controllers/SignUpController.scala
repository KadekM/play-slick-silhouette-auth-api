package controllers

import java.time.LocalDateTime
import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import model.core.{User, UserToken}
import model.core.UserToken.TokenAction
import model.exchange.{Bad, CreatePassword, Good, SignUp}
import persistence.dao.LoginInfoDao
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsError, JsString, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller, Result}
import service.{UserService, UserTokenService}
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * repository
  * Sign up user to the system
  */
class SignUpController @Inject() (silhouette: Silhouette[DefaultEnv],
    passwordHasher: PasswordHasher,
    translate: MessagesApi,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    authInfoRepository: AuthInfoRepository) extends Controller with ResponseHelpers {

  import formatting.exchange.rest._
  import play.api.libs.concurrent.Execution.Implicits._

  /**
    * Registers users in service and issues token in backend
    */
  def signUpRequestRegistration: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[SignUp].map { signUp ⇒
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)

      userService.retrieve(loginInfo).flatMap { // todo: this could just be retrieving loginInfo
        case Some(user) ⇒
          Future.successful(BadRequest(Json.toJson(Bad(message = translate("user.exists")))))

        case None ⇒
          val user = User(UUID.randomUUID.toString, signUp.identifier, signUp.firstName, signUp.lastName,
            User.State.Created)

          for {
            user ← userService.save(user)
            _ ← loginInfoDao.save(loginInfo, user.uuid) // todo: this should be in one trasaahction
            registrationToken ← userTokenService.issue(user.uuid, TokenAction.ActivateAccount) // TODO token
          } yield {
            // TODO: remove token from here, do not return it, so users have to visit email - in email activate link is not link to api
            Ok(Json.toJson(Good(registrationToken.token)))
          }
      }
    }.recoverTotal(badRequestWithMessage)
  }

  def createPassword(token: String) = Action.async(parse.json) { implicit request ⇒
    request.body.validate[CreatePassword].map { requestPw =>
      userTokenService.claim(token).flatMap {
        // TODO token
        case Some(UserToken(_, userUuid, expiresOn, UserToken.TokenAction.ActivateAccount)) if !isTokenExpired(expiresOn) ⇒

          for {
            _ <- userService.setState(userUuid, User.State.Activated)
            Some(user) <- userService.retrieve(userUuid) // todo: test what if none
            loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            authInfo = passwordHasher.hash(requestPw.password)
            _ <- authInfoRepository.add(loginInfo, authInfo)
          } yield {
              Ok(Json.toJson(Good("todo.message")))
          }

        /*  userService.setState(userUuid, User.State.Activated).flatMap { _ =>
            userService.retrieve(userUuid).flatMap {
              case Some(user) ⇒
                val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
                val authInfo = passwordHasher.hash(requestPw.password)
                authInfoRepository.add(loginInfo, authInfo).map { _ ⇒
                  Ok(Json.toJson(Good("todo.message")))
                }

              case None ⇒
                Future.successful(NotFound(Json.toJson(Bad(message = "todo.usernotfound"))))
            }
          }
          */

        case _ ⇒ Future.successful(NotFound(Json.toJson(Bad(message = "token.invalid"))))
      }
    }.recoverTotal(badRequestWithMessage)
  }

  private def isTokenExpired(expiresOn: LocalDateTime): Boolean = expiresOn.isBefore(LocalDateTime.now)
}
