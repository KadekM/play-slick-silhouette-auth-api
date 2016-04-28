package auth.api.formatting.exchange

import auth.api.model.exchange.Good
import play.api.libs.json._

object GoodFormats {
  val restFormat: OFormat[Good] = Json.format[Good]
}
