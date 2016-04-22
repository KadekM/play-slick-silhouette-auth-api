package formatting.exchange

import model.exchange.SignUp
import play.api.libs.json.Json

object SignUpFormats {
  val restFormat = Json.format[SignUp]
}
