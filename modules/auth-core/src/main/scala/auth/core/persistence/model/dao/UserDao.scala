package auth.core.persistence.model.dao

import java.util.UUID

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import com.mohiva.play.silhouette

import scala.concurrent.Future

trait UserDao {
  def find(loginInfo: silhouette.api.LoginInfo): Future[Option[User]]

  def find(userUuid: UUID): Future[Option[User]]

  def save(user: User): Future[User]

  def setState(userUuid: UUID, newState: UserState): Future[Boolean]

  def list(): Future[Seq[User]]
}
