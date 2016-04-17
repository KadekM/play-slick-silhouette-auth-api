package model.core.format

import model.core.RegistrationToken
import play.api.libs.json.Json

object RegistrationTokenFormats {
  val rest = Json.format[RegistrationToken]
}
