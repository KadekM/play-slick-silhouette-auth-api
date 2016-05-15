package auth.core.formatting

import auth.core.model.core.{Permission, PermissionToUser, User}
import User.UserState
import play.api.libs.json.Format

package object core {
  object Rest {
    implicit val permissionFormat: Format[Permission]             = PermissionFormats.rest
    implicit val permissionToUserFormat: Format[PermissionToUser] = PermissionToUserFormats.rest

    implicit val userStateFormat: Format[UserState] = UserFormat.restUserState
    implicit val userFormat: Format[User]           = UserFormat.rest
  }
}
