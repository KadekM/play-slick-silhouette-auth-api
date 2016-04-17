package model.exchange.format

import model.exchange.SignUp
import play.api.libs.json.Json

object SignUpFormats {
  val restFormat = Json.format[SignUp]
}
