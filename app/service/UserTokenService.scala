package service

import model.core.UserToken
import model.core.UserToken.UserTokenAction

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
  def issue(userUuid: String, action: UserTokenAction): Future[UserToken]

  /**
    * Claims token if found and returns it. Once token is claimed, it cannot be claimed again.
 *
    * @param token to search for
    * @return claimed token
    */
  def claim(token: String): Future[Option[UserToken]]
}


