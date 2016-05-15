package auth.api.controllers

import java.util.UUID

import auth.api.model.exchange._
import auth.api.service.{AuthService, ExtendedUserService}
import auth.api.service.AuthService._
import auth.core.DefaultEnv
import auth.core.service.authorization.PermissionsAuthorizer
import auth.direct.service.PermissionService
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.EmptyContent
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext
import scalaz._

class UsersController @Inject()(silhouette: Silhouette[DefaultEnv],
                                configuration: Configuration,
                                permissions: PermissionsAuthorizer,
                                userService: ExtendedUserService,
                                authService: AuthService,
                                permissionService: PermissionService)(
    implicit ec: ExecutionContext)
    extends Controller with ResponseHelpers {

  import auth.api.formatting.exchange.Rest._
  import auth.core.formatting.core.Rest._

  val newAccountTokenValidFor =
    configuration.underlying.getDuration("tokens.activate-account.validfor")

  /**
    * Lists all users
    */
  def listAll: Action[AnyContent] =
    Action.async { implicit req ⇒
      //silhouette.SecuredAction.async { implicit req =>
      //silhouette.SecuredAction(permissions.require(AccessAdmin)).async { implicit req ⇒
      userService.list().map { res ⇒
        Ok(Json.toJson(res))
      }
    }

  /**
    * Gets user with specified `uuid`
    */
  def get(uuid: String): Action[AnyContent] = Action.async {
    val user = userService.retrieve(UUID.fromString(uuid))

    user.map {
      case Some(u) ⇒
        Ok(Json.toJson(u))

      case None ⇒
        NotFound
    }
  }

  /**
    * Updates user details of `uuid`
    * TODO: authentication
    */
  def update(uuid: String): Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body
      .validate[UpdateUser]
      .map { model ⇒
        userService.update(UUID.fromString(uuid), model).map {
          case true  ⇒ Ok(EmptyContent())
          case false ⇒ BadRequest(EmptyContent())
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  /**
    * Gets user by login info
    */
  def getByLoginInfo(providerID: String, providerKey: String): Action[AnyContent] = Action.async {
    val user = userService.retrieve(LoginInfo(providerID, providerKey))

    user.map {
      case Some(u) ⇒
        Ok(Json.toJson(u))

      case None ⇒
        NotFound
    }
  }

  /**
    * Registers users in auth.direct.service and issues token in backend
    */
  def signUpRequestRegistration: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body
      .validate[SignUp]
      .map { signUp ⇒
        authService.createUserByCredentials(signUp, newAccountTokenValidFor.toHours) map {
          case -\/(UserAlreadyExists) ⇒
            Conflict(Json.toJson(Bad("user.exists")))
          case \/-(created) ⇒
            Created(Json.toJson(Json.obj("token" -> created.token)))
              .withHeaders(LOCATION              → auth.api.controllers.routes.UsersController
                  .get(created.uuid.toString)
                  .url)
        }
      }
      .recoverTotal(badRequestWithMessage)
  }
}
