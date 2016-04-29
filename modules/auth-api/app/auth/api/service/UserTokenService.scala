package auth.api.service

import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction

import scala.concurrent.Future

/**
  * Service for actions on user account
  */
trait UserTokenService {
  /**
    * Issues a new token. New token is stored so it can later be claimed.
 *
    * @return new token
    */
  def issue(userUuid: UUID, action: UserTokenAction): Future[UserToken]

  /**
    * Claims token if found and returns it. Once token is claimed, it cannot be claimed again.
 *
    * @param token to search for
    * @return claimed token
    */
  def claim(token: String): Future[Option[UserToken]]
}
