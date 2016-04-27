package persistence.model.dao

import model.core.UserToken
import model.core.UserToken.UserTokenAction

import scala.concurrent.Future

trait UserTokenDao {
  def issue(userUuid: String, action: UserTokenAction): Future[UserToken]

  def find(token: String): Future[Option[UserToken]]

  def remove(token: String): Future[Boolean]
}
