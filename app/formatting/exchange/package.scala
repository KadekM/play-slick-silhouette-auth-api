package formatting

import com.mohiva.play.silhouette.api.util.Credentials
import model.exchange._
import play.api.libs.json.{Format, OFormat}

package object exchange {
  object rest {
    implicit val badFormat: Format[Bad] = BadFormats.restFormat
    implicit val goodFormat: Format[Good] = GoodFormats.restFormat

    implicit val tokenFormat: Format[Token] = TokenFormats.restFormat

    implicit val signUpFormat: OFormat[SignUp] = SignUpFormats.restFormat
    implicit val createPasswordFormat: OFormat[CreatePassword] = CreatePasswordFormats.restForamt
    implicit val credentialsFormat: Format[Credentials] = CredentialsFormats.restFormat
  }
}
