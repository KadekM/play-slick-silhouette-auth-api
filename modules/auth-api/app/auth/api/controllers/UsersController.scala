package auth.api.controllers

import java.util.UUID

import auth.api.model.core.UserToken.TokenAction
import auth.api.model.exchange._
import auth.api.service.UserTokenService
import auth.core.DefaultEnv
import auth.core.model.core._
import auth.core.service.authorization.PermissionsAuthorizer
import auth.core.persistence.model.dao.LoginInfoDao
import auth.core.service.{ PermissionService, UserService }
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{ LoginInfo, Silhouette }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.Configuration
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Controller }

import scala.concurrent.{ ExecutionContext, Future }

class UsersController @Inject() (silhouette: Silhouette[DefaultEnv],
    configuration: Configuration,
    permissions: PermissionsAuthorizer,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    permissionService: PermissionService)(implicit ec: ExecutionContext) extends Controller with ResponseHelpers {

  import auth.api.formatting.exchange.rest._
  import auth.core.formatting.core.rest._

  val newAccountTokenValidFor = configuration.underlying.getDuration("tokens.activate-account.validfor")

  /**
    * Lists all users
    */
  def listAll: Action[AnyContent] =
    Action.async { implicit req =>
    //silhouette.SecuredAction.async { implicit req =>
    //silhouette.SecuredAction(permissions.require(AccessAdmin)).async { implicit req ⇒
      userService.list().map { res ⇒
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
            _ ← loginInfoDao.save(loginInfo, user.uuid) // todo: this should be in one transaction, move out
            registrationToken ← userTokenService.issue(user.uuid, TokenAction.ActivateAccount, newAccountTokenValidFor.toHours)
          } yield {
            // TODO: remove token from here, do not return it, so users have to visit email - in email activate link is not link to api
            Created(Json.toJson(Good(Json.obj("token" -> registrationToken.token))))
              .withHeaders("Location" -> auth.api.controllers.routes.UsersController.get(user.uuid.toString).url)
          }
      }
    }.recoverTotal(badRequestWithMessage)
  }
}
