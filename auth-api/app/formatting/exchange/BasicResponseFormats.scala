package formatting.exchange

import model.exchange.Bad
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsValue, Reads, Writes, _}

object BadFormats {
  val restFormat = {
    val reader: Reads[Bad] = (
      (__ \ "code").readNullable[Int] ~
      (__ \ "error").read[JsValue])(Bad.apply(_, _))

    val writer: Writes[Bad] = (
      (__ \ "status").write[String] ~
      (__ \ "code").writeNullable[Int] ~
      (__ \ "error").write[JsValue])(unlift(Bad.unapply _))

    Format(reader, writer)
  }
}