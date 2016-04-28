package auth.api.formatting.exchange

import auth.api.model.exchange.SignUp
import play.api.libs.json.Json

object SignUpFormats {
  val restFormat = Json.format[SignUp]
}
