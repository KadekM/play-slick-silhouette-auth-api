package model.exchange

import play.api.libs.json.{JsString, JsValue}


class Good(val message: JsValue) {
  def status = "ok"
}

object Good {
  def apply(message: String) = new Good(JsString(message))
  def apply(message: JsValue) = new Good(message)
  def unapply(good: Good) = Some((good.status, good.message))
}