package auth.api.formatting.exchange

import auth.api.model.exchange.SignIn
import play.api.libs.json.Json

object SignInFormats {
  val restFormat = Json.format[SignIn]
}
