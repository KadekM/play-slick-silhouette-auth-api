package auth.core.formatting.core

import auth.core.model.core.{Permission, PermissionToUser}
import play.api.libs.json.{Format, Json}

object PermissionToUserFormats {
  implicit val permissionFormat: Format[Permission] = PermissionFormats.rest
  val rest: Format[PermissionToUser]                = Json.format[PermissionToUser]
}
