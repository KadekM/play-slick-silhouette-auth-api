package auth.api.controllers

import auth.api.model.exchange.Good
import auth.core.DefaultEnv
import auth.core.model.core.AccessAdmin
import auth.core.service.authorization.PermissionsAuthorizer
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class VerifyController @Inject()(silhouette: Silhouette[DefaultEnv],
                                permissions: PermissionsAuthorizer)(implicit ec: ExecutionContext)
  extends Controller {

  import auth.api.formatting.exchange.rest._

  def verify: Action[AnyContent] = Action.async { implicit request ⇒
    silhouette.SecuredRequestHandler { x ⇒
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) ⇒
        Ok(data)
      case HandlerResult(r, None) ⇒ Unauthorized
    }
  }

  def verifyAdmin = silhouette.SecuredAction(permissions.require(AccessAdmin)) { implicit req ⇒
    Ok(Json.toJson(Good.empty))
  }
}

