package auth.api.formatting

import auth.api.model.exchange._
import play.api.libs.json._

package object exchange {
  object Rest {
    implicit val badFormat: Format[Bad]                = ResponsesFormats.badRestFormat
    implicit val signUpFormat: OFormat[SignUp]         = RequestsFormats.signUpRestFormat
    implicit val updateUserFormat: OFormat[UpdateUser] = RequestsFormats.updateUserRestFormat
    implicit val createPasswordFormat: OFormat[CreatePassword] =
      RequestsFormats.createPasswordRestForamt
    implicit val signInFormat: Format[SignIn] = RequestsFormats.signInRestFormat
    implicit val assignPermissionRequestFormat: OFormat[AssignPermission] =
      RequestsFormats.assignPermissionRestFormat

    implicit val tokenFormat: Format[Token] = ResponsesFormats.tokenRestFormat
  }
}
