package controllers

import auth.persistence.model.authorization.PermissionsAuthorizer
import auth.service.{ PermissionService, UserService }
import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{ Action, Controller }

//TODO: remove inject
class UsersController @Inject() (authorizer: PermissionsAuthorizer,
    usersService: UserService,
    permissionService: PermissionService) extends Controller with ResponseHelpers {

  import auth.formatting.core.rest._
  //todo ec
  import scala.concurrent.ExecutionContext.Implicits.global

  def listAll = Action.async { implicit request ⇒
    usersService.list.map { res ⇒
      Ok(Json.obj("users" -> res))
    }
  }
}
