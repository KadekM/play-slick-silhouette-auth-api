package auth.api.controllers

import auth.api.model.exchange._
import auth.core.DefaultEnv
import auth.core.model.core.AccessAdmin
import auth.core.service.authorization.PermissionsAuthorizer
import auth.core.service.PermissionService
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Controller }

import scala.concurrent.ExecutionContext

class PermissionsController @Inject() (
    silhouette: Silhouette[DefaultEnv],
    permissions: PermissionsAuthorizer,
    permissionService: PermissionService)(implicit ec: ExecutionContext) extends Controller with ResponseHelpers {
  import auth.api.formatting.exchange.rest._

  def grant: Action[AnyContent] =
  silhouette.SecuredAction(permissions.require(AccessAdmin)).async { implicit request ⇒
    //TODO: asJson.get
    request.body.asJson.get.validate[PermissionUserPair].map { perm ⇒
      permissionService.grant(perm.permission, perm.userUuid).map {
        case true  ⇒ Ok(Json.toJson(Good.empty)) // TODO: correct return value to resource
        case false ⇒ BadRequest(Json.toJson(Bad("not.granted")))
      }
    }.recoverTotal(badRequestWithMessage)
  }

  def revoke: Action[AnyContent] = ???

  def listPossible: Action[AnyContent] = ???

  def listAssigned: Action[AnyContent] = ???
}
