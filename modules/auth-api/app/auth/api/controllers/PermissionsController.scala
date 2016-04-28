package auth.api.controllers

import auth.api.model.exchange._
import auth.core.persistence.model.authorization.PermissionsAuthorizer
import auth.core.service.PermissionService
import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

class PermissionsController @Inject() (authorizer: PermissionsAuthorizer,
    permissionService: PermissionService) extends Controller with ResponseHelpers {
  //todo ec
  import scala.concurrent.ExecutionContext.Implicits.global
  import auth.api.formatting.exchange.rest._

  def grant: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[PermissionUserPair].map { perm ⇒
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
