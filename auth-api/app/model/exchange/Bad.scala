package model.exchange

import play.api.libs.json.{JsString, JsValue}

class Bad(val code: Option[Int], val error: JsValue) {
  def status = "err"
}

object Bad {
  def apply(code: Option[Int] = None, message: String) = new Bad(code, JsString(message))
  def apply(code: Option[Int], message: JsValue) = new Bad(code, message)
  def apply(message: JsValue) = new Bad(None, message)
  def unapply(bad: Bad) = Some((bad.status, bad.code, bad.error))
}
