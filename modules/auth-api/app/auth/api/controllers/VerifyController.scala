package auth.api.controllers

import auth.core.DefaultEnv
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

class VerifyController @Inject()(silhouette: Silhouette[DefaultEnv]) extends Controller {

  import play.api.libs.concurrent.Execution.Implicits._

  def verify: Action[AnyContent] = Action.async { implicit request ⇒
    silhouette.SecuredRequestHandler { x ⇒
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) ⇒
        Ok(data)
      case HandlerResult(r, None) ⇒ Forbidden
    }
  }
}
