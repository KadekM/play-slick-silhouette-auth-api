package auth.api.formatting

import auth.api.model.core.UserToken
import play.api.libs.json.Format

package object core {
  object Rest {
    implicit val userTokenFormat: Format[UserToken] = UserTokenFormats.rest
  }
}
