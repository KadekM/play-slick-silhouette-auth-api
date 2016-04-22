package formatting

import model.core.UserToken
import play.api.libs.json.Format

package object core {
  object rest {
    implicit val userTokenFormat: Format[UserToken] = UserTokenFormats.rest
  }
}
