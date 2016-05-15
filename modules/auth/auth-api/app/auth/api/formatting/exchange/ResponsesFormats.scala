package auth.api.formatting.exchange

import auth.api.model.exchange.{Bad, Token}
import play.api.libs.json.{Json, OFormat}

object ResponsesFormats {
  val badRestFormat: OFormat[Bad]     = Json.format[Bad]
  val tokenRestFormat: OFormat[Token] = Json.format[Token]
}
