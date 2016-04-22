package formatting.exchange

import model.exchange.Token
import play.api.libs.json.Json

object TokenFormats {
  val restFormat = Json.format[Token]
}
