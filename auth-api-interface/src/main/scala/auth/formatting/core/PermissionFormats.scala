package auth.formatting.core

import auth.model.core.{AccessAdmin, AccessBar, Permission}
import play.api.libs.json._

object PermissionFormats {

  val rest: Format[Permission] = new Format[Permission] {

    override def reads(json: JsValue): JsResult[Permission] = json match {
      case JsString(Permission.accessAdmin) ⇒ JsSuccess(AccessAdmin)
      case JsString(Permission.accessBar)   ⇒ JsSuccess(AccessBar)
      case _                            ⇒ JsError(s"Can't parse $json to UserTokenAction")
    }

    override def writes(o: Permission): JsValue = o match {
      case AccessAdmin => JsString(Permission.accessAdmin)
      case AccessBar => JsString(Permission.accessBar)
    }
  }

}
