package auth.persistence.model.dao

import auth.model.core.User
import com.mohiva.play.silhouette
import User.UserState

import scala.concurrent.Future

trait UserDao {
  def find(loginInfo: silhouette.api.LoginInfo): Future[Option[User]]

  def find(userUuid: String): Future[Option[User]]

  def save(user: User): Future[User]

  def setState(userUuid: String, newState: UserState): Future[Boolean]
}
