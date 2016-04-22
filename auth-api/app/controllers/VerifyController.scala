package controllers

import auth.DefaultEnv
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import play.api.mvc.{ Action, AnyContent, Controller }

import scala.concurrent.Future

class VerifyController(silhouette: Silhouette[DefaultEnv]) extends Controller {

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

