package auth.core.formatting.core

import auth.core.model.core.User
import User.UserState
import play.api.libs.functional.syntax._
import play.api.libs.json._

object UserFormat {
  //todo
  implicit val restUserState = new Format[UserState] {
    override def reads(json: JsValue): JsResult[UserState] = json match {
      case JsString(User.State.created)     ⇒ JsSuccess(User.State.Created)
      case JsString(User.State.activated)   ⇒ JsSuccess(User.State.Activated)
      case JsString(User.State.deactivated) ⇒ JsSuccess(User.State.Deactivated)
      case _                                ⇒ JsError(s"Can't parse $json to UserTokenAction")
    }

    override def writes(o: UserState): JsValue = o match {
      case User.State.Created     ⇒ JsString(User.State.created)
      case User.State.Activated   ⇒ JsString(User.State.activated)
      case User.State.Deactivated ⇒ JsString(User.State.deactivated)
    }
  }

  val rest: Format[User] = {
    import play.api.libs.json.Writes._

    val reads: Reads[User] = (
      (__ \ "uuid").read[String] ~
      (__ \ "email").read[String] ~
      (__ \ "firstName").read[String] ~
      (__ \ "lastName").read[String] ~
      (__ \ "state").read[UserState])(User.apply _)

    val write: Writes[User] = (
      (__ \ "uuid").write[String] ~
      (__ \ "email").write[String] ~
      (__ \ "firstName").write[String] ~
      (__ \ "lastName").write[String] ~
      (__ \ "state").write[UserState])(unlift(User.unapply))

    Format(reads, write)
  }
}
