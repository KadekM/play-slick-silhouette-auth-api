package auth.api.formatting.exchange

import auth.api.model.exchange._
import auth.core.formatting.core
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object RequestsFormats {
  import core.Rest.permissionFormat
  val assignPermissionRestFormat: OFormat[AssignPermission] = Json.format[AssignPermission]

  val createPasswordRestForamt: OFormat[CreatePassword] = {
    // Single field problems case classes problem workaround:
    val validation = Format
      .of[String]
      .filter(ValidationError("Weak password"))(_.length > 5)
      (__ \ "password")
      .format(validation)
      .inmap(CreatePassword.apply _, unlift(CreatePassword.unapply))
  }

  val signInRestFormat: OFormat[SignIn] = Json.format[SignIn]

  val signUpRestFormat: OFormat[SignUp] = ((__ \ "identifier").format[String](email) ~
      (__ \ "firstName").format[String] ~ (__ \ "lastName").format[String])(
      SignUp.apply, unlift(SignUp.unapply))

  val updateUserRestFormat: OFormat[UpdateUser] = Json.format[UpdateUser]
}
