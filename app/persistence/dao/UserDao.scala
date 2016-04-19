package persistence.dao

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import model.core.User
import model.core.User.UserState

import scala.concurrent.Future

trait UserDao {
  def find(loginInfo: LoginInfo): Future[Option[User]]

  def find(userUuid: String): Future[Option[User]]

  def save(user: User): Future[User]

  def setState(userUuid: String, newState: UserState): Future[Boolean]
}
