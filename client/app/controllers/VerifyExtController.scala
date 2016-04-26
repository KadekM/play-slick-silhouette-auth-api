package controllers

import auth.DefaultEnv
import auth.model.core.AccessAdmin
import auth.persistence.model.authorization.impl.WithDbPermission
import auth.persistence.model.dao.PermissionDao
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

class VerifyExtController @Inject()(silhouette: Silhouette[DefaultEnv],
                                    todoDao: PermissionDao) extends Controller {

  import play.api.libs.concurrent.Execution.Implicits._

  def verify: Action[AnyContent] = Action.async { implicit request =>
    println("starting")
    silhouette.SecuredRequestHandler { x =>
      Future.successful(HandlerResult(Ok("All's good"), Some(x.identity.email)))
    }.map {
      case HandlerResult(r, Some(data)) =>
        Ok(data)
      case HandlerResult(r, None) =>
        println(r)
        Forbidden
    }
  }

  // TODO dependance on impl
  def verifyAdmin = silhouette.SecuredAction(WithDbPermission(AccessAdmin, todoDao)).async { implicit req =>
    Future.successful { Ok("a") }
  }
}

