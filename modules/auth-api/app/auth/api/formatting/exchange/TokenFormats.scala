package auth.api.formatting.exchange

import auth.api.model.exchange.Token
import play.api.libs.json.Json

object TokenFormats {
  val restFormat = Json.format[Token]
}
