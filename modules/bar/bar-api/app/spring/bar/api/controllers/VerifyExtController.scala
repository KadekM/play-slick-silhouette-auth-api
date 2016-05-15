package spring.bar.api.controllers

import auth.core.DefaultEnv
import auth.core.model.core.Permission.AccessAdmin
import auth.core.model.core._
import auth.core.service.authorization.PermissionsAuthorizer
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class VerifyExtController @Inject()(
    silhouette: Silhouette[DefaultEnv],
    permissions: PermissionsAuthorizer
)(implicit ec: ExecutionContext)
    extends Controller {

  def verify: Action[AnyContent] = Action.async { implicit request ⇒
    silhouette.SecuredRequestHandler { x ⇒
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) ⇒
        Ok(data)
      case HandlerResult(r, None) ⇒
        Forbidden
    }
  }

  def verifyAdmin = silhouette.SecuredAction(permissions.require(AccessAdmin)) { implicit req ⇒
    Ok(Json.toJson(""))
  }
}
