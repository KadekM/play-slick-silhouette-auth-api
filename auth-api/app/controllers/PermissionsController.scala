package controllers

import auth.persistence.model.authorization.PermissionsAuthorizer
import auth.service.PermissionService
import com.google.inject.Inject
import model.exchange._
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, Controller }

//TODO: remove inject
class PermissionsController @Inject() (authorizer: PermissionsAuthorizer,
    permissionService: PermissionService) extends Controller with ResponseHelpers {

  import formatting.exchange.rest._
  //todo ec
  import scala.concurrent.ExecutionContext.Implicits.global

  def grant: Action[JsValue] = Action.async(parse.json) { implicit request ⇒
    request.body.validate[PermissionUserPair].map { perm ⇒
      permissionService.grant(perm.permission, perm.userUuid).map {
        case true  ⇒ Ok(Json.toJson(Good.empty))
        case false ⇒ BadRequest(Json.toJson(Bad("not.granted")))
      }
    }.recoverTotal(badRequestWithMessage)
  }

  def revoke: Action[AnyContent] = ???

  def listPossible: Action[AnyContent] = ???

  def listAssigned: Action[AnyContent] = ???
}
