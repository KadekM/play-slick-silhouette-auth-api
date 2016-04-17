package model.core

import model.exchange.format.TokenFormats
import play.api.libs.json.Format

package object format {
  object rest {
    implicit val registrationTokenFormat: Format[RegistrationToken] = RegistrationTokenFormats.rest
  }
}
