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
  def issue(userUuid: UUID, action: UserTokenAction, forHours: Long): Future[UserToken]

  /**
    * Finds token if found and returns it.
    *
    * @param token to search for
    * @return found token
    */
  def find(token: String): Future[Option[UserToken]]

  /**
    * Consumes token if found. Onces token is consumed, it cannot be found again.
    *
    * @param token to search for
    * @return true if consumed, otherwise false
    */
  def consume(token: String): Future[Boolean]
}
