package auth.api.controllers

import java.util.UUID

import auth.api.model.core.UserToken.TokenAction
import auth.api.model.exchange._
import auth.api.service.UserTokenService
import auth.core.model.core._
import auth.core.persistence.model.authorization.PermissionsAuthorizer
import auth.core.persistence.model.dao.LoginInfoDao
import auth.core.service.{PermissionService, UserService}
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

class UsersController @Inject() (authorizer: PermissionsAuthorizer,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    permissionService: PermissionService) extends Controller with ResponseHelpers {

  //todo ec
  import scala.concurrent.ExecutionContext.Implicits.global
  import auth.api.formatting.exchange.rest._
  import auth.core.formatting.core.rest._

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
    * Registers users in auth.core.service and issues token in backend
    */
  def signUpRequestRegistration: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[SignUp].map { signUp ⇒
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)

      userService.retrieve(loginInfo).flatMap {
        case Some(user) ⇒
          Future.successful(Conflict(Json.toJson(Bad("user.exists"))))
        case None ⇒
          val user = User(UUID.randomUUID, signUp.identifier, signUp.firstName, signUp.lastName,
            User.State.Created)

          for {
            user ← userService.save(user)
            _ ← loginInfoDao.save(loginInfo, user.uuid) // todo: this should be in one transaction
            registrationToken ← userTokenService.issue(user.uuid, TokenAction.ActivateAccount) // TODO token type
          } yield {
            // TODO: remove token from here, do not return it, so users have to visit email - in email activate link is not link to api
            Created(Json.toJson(Good(Json.obj("token" -> registrationToken.token))))
              .withHeaders("Location" -> auth.api.controllers.routes.UsersController.get(user.uuid.toString).url)
          }
      }
    }.recoverTotal(badRequestWithMessage)
  }
}
