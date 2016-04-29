package someclient.controllers

import auth.core.DefaultEnv
import auth.core.model.core.AccessAdmin
import auth.core.persistence.model.authorization.PermissionsAuthorizer
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

// TODO: remove inject
class VerifyExtController @Inject() (silhouette: Silhouette[DefaultEnv],
  permissions: PermissionsAuthorizer)(implicit ec: ExecutionContext)
    extends Controller {

  def verify: Action[AnyContent] = Action.async { implicit request ⇒
    println("starting")
    silhouette.SecuredRequestHandler { x ⇒
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) ⇒
        Ok(data)
      case HandlerResult(r, None) ⇒
        println(r)
        Forbidden
    }
  }

  def verifyAdmin = silhouette.SecuredAction(permissions.require(AccessAdmin)).async { implicit req ⇒
    Future.successful { Ok("a") }
  }
}

