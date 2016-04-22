package persistence.model.dao

import model.core.UserToken
import model.core.UserToken.UserTokenAction

import scala.concurrent.Future

trait UserTokenDao {
  def issue(userUuid: String, action: UserTokenAction): Future[UserToken]

  def claim(token: String): Future[Option[UserToken]]
}
