package formatting.exchange

import model.exchange.PermissionUserPair
import play.api.libs.json.Json

object PermissionUserPairFormat {
  import auth.formatting.core.rest.permissionFormat
  val rest = Json.format[PermissionUserPair]
}
