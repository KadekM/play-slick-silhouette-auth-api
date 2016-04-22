package formatting.core

import java.time.LocalDateTime

import model.core.UserToken
import model.core.UserToken.TokenAction._
import model.core.UserToken.UserTokenAction
import play.api.libs.functional.syntax._
import play.api.libs.json._

object UserTokenFormats {

  object UserTokenAction {
    val rest: Format[UserTokenAction] = {
      val reads: Reads[UserTokenAction] = new Reads[UserTokenAction] {
        override def reads(json: JsValue): JsResult[UserTokenAction] = json match {
          case JsString("activate_account") ⇒ JsSuccess(ActivateAccount)
          case JsString("reset_password")   ⇒ JsSuccess(ResetPassword)
          case _                            ⇒ JsError(s"Can't parse $json to UserTokenAction")
        }
      }

      val writes: Writes[UserTokenAction] = new Writes[UserTokenAction] {
        override def writes(o: UserTokenAction): JsValue = o match {
          case ActivateAccount ⇒ JsString("activate_account")
          case ResetPassword   ⇒ JsString("reset_password")
        }
      }

      Format(reads, writes)
    }
  }

  val rest: OFormat[UserToken] = {
    implicit val userTokenAction: Format[UserTokenAction] = UserTokenAction.rest

     ((__ \ "token").format[String] ~
      (__ \ "userUuid").format[String] ~
      (__ \ "expiresOn").format[LocalDateTime] ~
      (__ \ "tokenAction").format[UserTokenAction])(UserToken.apply, unlift(UserToken.unapply))
  }
}
