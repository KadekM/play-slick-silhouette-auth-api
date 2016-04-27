package model.exchange

import play.api.libs.json.{JsString, JsValue}

/**
  * Signalizes problem on server
  * @param status represents details of the problem, such as `user.exists`
  * @param details possible details, such as missing field in json
  */

final case class Bad(status: String, details: Option[JsValue] = None)

object Bad {
  def apply(status: String, simpleDetails: String): Bad = Bad(status, Some(JsString(simpleDetails)))
  def apply(status: String, details: JsValue): Bad = Bad(status, Some(details))
}
