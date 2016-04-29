package auth.core.service.impl

import java.util.UUID

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import auth.core.persistence.model.dao.UserDao
import auth.core.service.UserService

import scala.concurrent.Future

/**
  * Stores users in db.
  */
class UserServiceImpl(userDao: UserDao) extends UserService {
  override def save(user: User): Future[User] = userDao.save(user)

  override def save(profile: CommonSocialProfile): Future[User] = ???

  override def retrieve(loginInfo: silhouette.api.LoginInfo): Future[Option[User]] = userDao.find(loginInfo)

  override def retrieve(userUuid: UUID): Future[Option[User]] = userDao.find(userUuid)

  override def setState(userUuid: UUID, newState: UserState): Future[Boolean] = userDao.setState(userUuid, newState)

  override def list(): Future[Seq[User]] = userDao.list
}
