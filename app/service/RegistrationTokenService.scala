package service

import model.core.RegistrationToken

import scala.concurrent.Future

/**
  * Service for confirmation of user account
  */
trait RegistrationTokenService {
  /**
    * Issues a new token. New token is stored so it can later be claimed.
    * @return new token
    */
  def issue(userUuid: String): Future[RegistrationToken]

  /**
    * Claims token if found and returns it. Once token is claimed, it cannot be claimed again.
    * @param token to search for
    * @return claimed token
    */
  def claim(token: String, userUuid: String): Future[Option[RegistrationToken]]
}


