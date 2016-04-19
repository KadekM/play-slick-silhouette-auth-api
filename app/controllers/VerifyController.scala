package controllers

import java.time.LocalDateTime

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import model.core.{User, UserToken}
import model.exchange.{Bad, Good}
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, Result}
import service.{UserService, UserTokenService}
import utils.auth.DefaultEnv

import scala.concurrent.Future

class VerifyController @Inject()(silhouette: Silhouette[DefaultEnv]) extends Controller {

  import play.api.libs.concurrent.Execution.Implicits._

  def verify: Action[AnyContent] = Action.async { implicit request =>
    silhouette.SecuredRequestHandler { x =>
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) =>
        Ok(data)
      case HandlerResult(r, None) => Forbidden
    }
  }
}

