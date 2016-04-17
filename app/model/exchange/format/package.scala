package model.exchange

import com.mohiva.play.silhouette.api.util.Credentials
import model.exchange.format.TokenFormats
import play.api.libs.json.{Format, OFormat}

package object format {
  object rest {
    implicit val badFormat: Format[Bad] = BadFormats.restFormat
    implicit val goodFormat: Format[Good] = GoodFormats.restFormat

    implicit val tokenFormat: Format[Token] = TokenFormats.restFormat

    implicit val signUpFormat: OFormat[SignUp] = SignUpFormats.restFormat
    implicit val credentialsFormat: Format[Credentials] = CredentialsFormats.restFormat
  }
}
