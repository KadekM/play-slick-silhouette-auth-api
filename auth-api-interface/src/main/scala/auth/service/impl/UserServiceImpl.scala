package auth.service.impl

import auth.model.core.User
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import User.UserState
import auth.persistence.model.dao.UserDao
import auth.service.UserService

import scala.concurrent.Future

/**
  * Stores users in db.
  */
class UserServiceImpl(userDao: UserDao) extends UserService {
  override def save(user: User): Future[User] = userDao.save(user)

  override def save(profile: CommonSocialProfile): Future[User] = ???

  override def retrieve(loginInfo: silhouette.api.LoginInfo): Future[Option[User]] = userDao.find(loginInfo)

  override def retrieve(userUuid: String): Future[Option[User]] = userDao.find(userUuid)

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = userDao.setState(userUuid, newState)
}
