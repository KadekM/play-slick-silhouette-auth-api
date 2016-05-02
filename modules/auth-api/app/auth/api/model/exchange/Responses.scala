package auth.api.model.exchange

import play.api.libs.json.{JsString, JsValue}

sealed trait GenericResponse

/**
  * Signalizes problem on server
  * @param status represents details of the problem, such as `user.exists`
  * @param details possible details, such as missing field in json
  */

final case class Bad(status: String, details: Option[JsValue] = None) extends GenericResponse

object Bad {
  def apply(status: String, simpleDetails: String): Bad = Bad(status, Some(JsString(simpleDetails)))
  def apply(status: String, details: JsValue): Bad = Bad(status, Some(details))
  val empty: Bad = Bad("", None)
  val invalidJson: Bad = Bad("invalid.json")
}

/**
  * In case all goes well provides details, such as when new article gets created {"article_id": 12345}
  * @param details for consumer
  */
final case class Good(details: Option[JsValue]) extends GenericResponse

object Good {
  def apply(message: String): Good = Good(Some(JsString(message)))
  def apply(message: JsValue): Good = Good(Some(message))
  val empty = Good(None)
}
