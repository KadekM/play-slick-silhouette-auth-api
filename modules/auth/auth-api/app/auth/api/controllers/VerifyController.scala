package auth.api.controllers

import auth.core.DefaultEnv
import auth.core.model.core.Permission.AccessAdmin
import auth.core.service.authorization.PermissionsAuthorizer
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.mvc.Results.EmptyContent
import play.api.mvc.{Action, AnyContent, Controller, Results}

import scala.concurrent.{ExecutionContext, Future}

class VerifyController @Inject()(
    silhouette: Silhouette[DefaultEnv],
    permissions: PermissionsAuthorizer
)(implicit ec: ExecutionContext)
    extends Controller {

  import auth.api.formatting.exchange.Rest._

  def verify: Action[AnyContent] = Action.async { implicit request ⇒
    silhouette.SecuredRequestHandler { x ⇒
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) ⇒
        Ok(data)
      case HandlerResult(r, None) ⇒ Unauthorized
    }
  }

  def verifyRoles: Action[AnyContent] = ???

  def verifyAdmin: Action[AnyContent] =
    silhouette.SecuredAction(permissions.require(AccessAdmin)) { implicit req ⇒
      Ok(EmptyContent())
    }
}
