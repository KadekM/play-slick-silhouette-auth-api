package controllers

import java.util.UUID

import auth.model.core.User
import auth.persistence.model.authorization.PermissionsAuthorizer
import auth.persistence.model.dao.LoginInfoDao
import auth.service.{ PermissionService, UserService }
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import model.core.UserToken.TokenAction
import model.exchange.{ Bad, Good, SignUp }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Controller }
import service.UserTokenService

import scala.concurrent.Future

class UsersController @Inject() (authorizer: PermissionsAuthorizer,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    permissionService: PermissionService) extends Controller with ResponseHelpers {

  //todo ec
  import auth.formatting.core.rest._
  import formatting.exchange.rest._
  import scala.concurrent.ExecutionContext.Implicits.global

  /**
    * Lists all users
    */
  def listAll: Action[AnyContent] = Action.async { implicit request ⇒
    userService.list.map { res ⇒
      Ok(Json.obj("users" -> res))
    }
  }

  /**
    * Gets user with specified `uuid`
    */
  def get(uuid: String): Action[JsValue] = ???

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
            Created(Json.toJson(Good(Json.obj("token" -> registrationToken.token))))
              .withHeaders("Location" -> controllers.routes.UsersController.get(user.uuid).url)
          }
      }
    }.recoverTotal(badRequestWithMessage)
  }
}
