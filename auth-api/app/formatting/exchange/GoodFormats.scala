package formatting.exchange

import model.exchange.Good
import play.api.libs.functional.syntax._
import play.api.libs.json._

object GoodFormats {
  implicit val restFormat: Format[Good] = {
    val reads: Reads[Good] = (__ \ "message").read[JsValue].map(Good.apply)

    import play.api.libs.json.Writes._
    val writes: Writes[Good] = (
      (__ \ "status").write[String] ~
      (__ \ "message").write[JsValue])(unlift(Good.unapply))

    Format(reads, writes)
  }
}
