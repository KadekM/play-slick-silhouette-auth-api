package auth.api.model.dao

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction

import scala.concurrent.Future

trait UserTokenDao {
  def issue(userUuid: String, action: UserTokenAction): Future[UserToken]

  def find(token: String): Future[Option[UserToken]]

  def remove(token: String): Future[Boolean]
}
