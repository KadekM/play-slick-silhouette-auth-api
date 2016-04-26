package controllers

import auth.persistence.model.authorization.PermissionsAuthorizer
import auth.service.PermissionService
import com.google.inject.Inject
import play.api.mvc.{ Action, AnyContent, Controller }

//TODO: remove inject
class PermissionsController @Inject() (authorizer: PermissionsAuthorizer,
    permissionService: PermissionService) extends Controller {

  def grant: Action[AnyContent] = ???

  def revoke: Action[AnyContent] = ???

  def listPossible: Action[AnyContent] = ???

  def listAssigned: Action[AnyContent] = ???
}
