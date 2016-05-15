package auth.api.controllers

import java.util.UUID

import auth.api.model.exchange._
import auth.core.DefaultEnv
import auth.core.model.core.Permission
import auth.core.service.authorization.PermissionsAuthorizer
import auth.direct.service.PermissionService
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.Json
import play.api.mvc.Results.EmptyContent
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class PermissionsController @Inject()(
    silhouette: Silhouette[DefaultEnv],
    permissions: PermissionsAuthorizer,
    permissionService: PermissionService)(implicit ec: ExecutionContext)
    extends Controller with ResponseHelpers {
  import auth.api.formatting.exchange.Rest._
  import auth.core.formatting.core.Rest._

  def grant(uuid: String): Action[AnyContent] =
    Action.async { implicit request ⇒
      //TODO
      //silhouette.SecuredAction(permissions.require(AccessAdmin)).async { implicit request ⇒
      //TODO: asJson.get
      request.body.asJson.get
        .validate[AssignPermission]
        .map { perm ⇒
          permissionService.grant(perm.permission, UUID.fromString(uuid)).map {
            case true ⇒
              Created(EmptyContent()).withHeaders(
                  LOCATION -> auth.api.controllers.routes.PermissionsController
                    .revoke(perm.permission.toString, uuid)
                    .url)
            case false ⇒ Conflict(Json.toJson(Bad("already.exists")))
          }
        }
        .recoverTotal(badRequestWithMessage)
    }

  def getForUser(uuid: String): Action[AnyContent] = Action.async {
    //implicit val r = Json.writes[Seq[PermissionToUser]]
    val permissions = permissionService.allOfUser(UUID.fromString(uuid))

    permissions.map { ps ⇒
      Ok(Json.toJson(ps))
    }
  }

  def revoke(permission: String, uuid: String): Action[AnyContent] = Action.async {
    val removed =
      permissionService.revoke(Permission.fromString(permission).get, UUID.fromString(uuid))

    removed.map {
      case true  ⇒ Ok(EmptyContent())
      case false ⇒ NotFound(Json.toJson(Bad.empty))
    }
  }

  def listPossible: Action[AnyContent] = Action.async {
    val possible = permissionService.allPossible()
    possible.map(ps ⇒ Ok(Json.toJson(ps)))
  }

  def listAssigned: Action[AnyContent] = Action.async {
    val assigned = permissionService.allAssigned()
    assigned.map(ps ⇒ Ok(Json.toJson(ps)))
  }
}
