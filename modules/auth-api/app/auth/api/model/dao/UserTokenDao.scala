package auth.api.model.dao

import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction

import scala.concurrent.Future

trait UserTokenDao {
  def issue(userUuid: UUID, action: UserTokenAction): Future[UserToken]

  def find(token: String): Future[Option[UserToken]]

  def remove(token: String): Future[Boolean]
}
