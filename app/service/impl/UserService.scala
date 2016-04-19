package service.impl

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import model.core.User
import model.core.User.UserState
import persistence.dao.UserDao
import service.UserService

import scala.concurrent.Future

/**
  * Stores users in db.
  */
// TODO: later should run these queries
class UserServiceImpl @Inject() (userDao: UserDao) extends UserService {
  override def save(user: User): Future[User] = userDao.save(user)

  override def save(profile: CommonSocialProfile): Future[User] = ???

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDao.find(loginInfo)

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = userDao.setState(userUuid, newState)
}

/**
  * Stores users in memory.
  * Not thread safe. Usage in production is discouraged (as it needs to be singleton).
  */
class InMemoryUserServiceImpl extends UserService {
  import scala.collection.mutable._
  private[this] val users: HashMap[LoginInfo, User] = HashMap.empty[LoginInfo, User]

  override def save(user: User): Future[User] = {
    users += user.loginInfo -> user

    Future.successful(user)
  }

  override def save(profile: CommonSocialProfile): Future[User] = ???

  // iterating over hashmap => very slow
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val user = users.get(loginInfo)
    Future.successful(user)
  }

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = {
    val userOpt = users.find(_._2.uuid == userUuid)
    val setOp = userOpt match {
      case Some((key, user)) ⇒
        users.remove(key)
        users += key -> user.copy(state = newState)
        true
      case None ⇒
        false
    }

    Future.successful(setOp)
  }
}
