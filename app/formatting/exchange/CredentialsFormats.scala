package formatting.exchange

import com.mohiva.play.silhouette.api.util.Credentials
import play.api.libs.json.Json

object CredentialsFormats {
  val restFormat = Json.format[Credentials]
}
