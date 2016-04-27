package model.exchange

import play.api.libs.json.{JsString, JsValue}

/**
  * In case all goes well provides details, such as when new article gets created {"article_id": 12345}
  * @param details for consumer
  */
final case class Good(details: Option[JsValue])

object Good {
  def apply(message: String): Good = Good(Some(JsString(message)))
  def apply(message: JsValue): Good = Good(Some(message))
  val empty = Good(None)
}
