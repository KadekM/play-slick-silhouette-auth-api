package auth.direct.service.impl

import java.util.UUID

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import auth.direct.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import auth.direct.service.UserService
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Stores users in db.
  */
class UserServiceImpl(
    protected val dbConfigProvider: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends UserService with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  val api = new Api()(ec)

  override def save(user: User): Future[User] = {
    db.run(api.Users.save(user))
  }

  override def save(profile: CommonSocialProfile): Future[User] = ???

  override def retrieve(loginInfo: silhouette.api.LoginInfo): Future[Option[User]] =
    db.run(api.Users.find(loginInfo))

  override def retrieve(userUuid: UUID): Future[Option[User]] =
    db.run(api.Users.find(userUuid))

  override def setState(userUuid: UUID, newState: UserState): Future[Boolean] =
    db.run(api.Users.setState(userUuid, newState))

  override def list(): Future[Seq[User]] =
    db.run(usersQuery.result)
}
