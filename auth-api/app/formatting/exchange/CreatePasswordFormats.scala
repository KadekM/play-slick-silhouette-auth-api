package formatting.exchange

import model.exchange.CreatePassword
import play.api.libs.json._

object CreatePasswordFormats {
  val restForamt: OFormat[CreatePassword] = Json.format[CreatePassword]
}
