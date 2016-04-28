package auth.api.formatting.exchange

import auth.api.model.exchange.Bad
import play.api.libs.json.{Json, OFormat}

object BadFormats {
  val restFormat: OFormat[Bad] = Json.format[Bad]
}