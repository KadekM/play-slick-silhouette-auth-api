package auth.core.persistence.model.dao

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import com.mohiva.play.silhouette

import scala.concurrent.Future

trait UserDao {
  def find(loginInfo: silhouette.api.LoginInfo): Future[Option[User]]

  def find(userUuid: String): Future[Option[User]]

  def save(user: User): Future[User]

  def setState(userUuid: String, newState: UserState): Future[Boolean]

  def list(): Future[Seq[User]]
}
