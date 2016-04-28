package auth.api.formatting.exchange

import auth.api.model.exchange.PermissionUserPair
import play.api.libs.json.Json

object PermissionUserPairFormat {
  import auth.core.formatting.core.rest.permissionFormat
  val rest = Json.format[PermissionUserPair]
}
