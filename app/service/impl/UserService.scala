package service.impl

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import model.core.User
import service.UserService

import scala.concurrent.Future

/**
  * Stores users in db.
  */
// TODO: dao-s - should not access directly db
class UserServiceImpl extends UserService {
  override def save(user: User): Future[User] = ???

  override def save(profile: CommonSocialProfile): Future[User] = ???

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = ???
}

/**
  * Stores users in memory.
  * Not thread safe. Usage in production is discouraged (as it needs to be singleton).
  */
class InMemoryUserServiceImpl extends UserService {
  import scala.collection.mutable._
  private[this] val users: HashMap[String, User] = HashMap.empty[String, User]

  override def save(user: User): Future[User] = {
    users += user.uuid.toString -> user

    Future.successful(user)
  }

  override def save(profile: CommonSocialProfile): Future[User] = ???

  // iterating over hashmap => very slow
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val user = users.find {
      case (_, user) => user.loginInfo == loginInfo
    }.map(_._2)

    Future.successful(user)
  }
}
