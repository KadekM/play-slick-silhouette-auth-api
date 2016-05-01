package auth.core.formatting.core

import java.util.UUID

import auth.core.model.core.User
import User.UserState
import play.api.libs.functional.syntax._
import play.api.libs.json._

object UserFormat {
  implicit val restUserState = new Format[UserState] {
    override def reads(json: JsValue): JsResult[UserState] = json match {
      case JsString(x) ⇒ User.State
        .fromString(x)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"No state found for $json"))
      case _ ⇒ JsError(s"Can't parse $json")
    }

    override def writes(o: UserState): JsValue = JsString(o.toString)
  }

  val rest: Format[User] = {
    import play.api.libs.json.Writes._

    val reads: Reads[User] = (
      (__ \ "uuid").read[UUID] ~
      (__ \ "email").read[String] ~
      (__ \ "firstName").read[String] ~
      (__ \ "lastName").read[String] ~
      (__ \ "state").read[UserState])(User.apply _)

    val write: Writes[User] = (
      (__ \ "uuid").write[UUID] ~
      (__ \ "email").write[String] ~
      (__ \ "firstName").write[String] ~
      (__ \ "lastName").write[String] ~
      (__ \ "state").write[UserState])(unlift(User.unapply))

    Format(reads, write)
  }
}
