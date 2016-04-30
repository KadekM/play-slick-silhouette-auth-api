package auth.core.service.impl

import java.util.UUID

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import auth.core.persistence.model.repo.UserRepo
import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess}
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import auth.core.service.UserService

import scala.concurrent.Future

/**
  * Stores users in db.
  */
class UserServiceImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider, userRepo: UserRepo)
  extends UserService with AuthDbAccess {

  override def save(user: User): Future[User] = db.run(userRepo.save(user))

  override def save(profile: CommonSocialProfile): Future[User] = ???

  override def retrieve(loginInfo: silhouette.api.LoginInfo): Future[Option[User]] = db.run(userRepo.find(loginInfo))

  override def retrieve(userUuid: UUID): Future[Option[User]] = db.run(userRepo.find(userUuid))

  override def setState(userUuid: UUID, newState: UserState): Future[Boolean] = db.run(userRepo.setState(userUuid, newState))

  override def list(): Future[Seq[User]] = db.run(userRepo.list)
}
