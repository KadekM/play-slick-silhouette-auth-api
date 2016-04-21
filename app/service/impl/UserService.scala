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

  override def retrieve(userUuid: String): Future[Option[User]] = userDao.find(userUuid)

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = userDao.setState(userUuid, newState)
}
