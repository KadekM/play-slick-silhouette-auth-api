package auth.formatting
import auth.model.core.User.UserState
import auth.model.core.{Permission, User}
import play.api.libs.json.Format

package object core {
  object rest {
    implicit val permissionFormat: Format[Permission] = PermissionFormats.rest

    implicit val userStateFormat: Format[UserState] = UserFormat.restUserState
    implicit val userFormat: Format[User] = UserFormat.rest
  }
}
