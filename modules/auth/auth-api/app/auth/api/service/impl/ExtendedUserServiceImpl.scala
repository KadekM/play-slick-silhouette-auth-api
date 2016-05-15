package auth.api.service.impl

import java.util.UUID

import auth.api.model.exchange.UpdateUser
import auth.api.service.ExtendedUserService
import auth.direct.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import auth.direct.service.impl.UserServiceImpl

import scala.concurrent.{ExecutionContext, Future}

/**
  * Provides method to also update users
  */
class ExtendedUserServiceImpl(dbConfigProvider: AuthDatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends UserServiceImpl(dbConfigProvider) with ExtendedUserService with AuthDbAccess
    with CoreAuthTablesDefinitions {

  import driver.api._

  // TODO: temporary implementation, fix redundant roundtrip and boilerplate
  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Option2Iterable"))
  override def update(userUuid: UUID, update: UpdateUser): Future[Boolean] = {
    val findUser = usersQuery.filter(_.uuid === userUuid)

    val firstNameUpdate = update.firstName.map { v ⇒
      findUser.map(u ⇒ u.firstName).update(v)
    }

    val lastNameUpdate = update.lastName.map { v ⇒
      findUser.map(u ⇒ u.lastName).update(v)
    }

    val emailUpdate = update.email.map { v ⇒
      throw new NotImplementedError("updating email!, what should happen?")
      findUser.map(u ⇒ u.email).update(v)
    }

    Seq(firstNameUpdate, lastNameUpdate, emailUpdate)
      .flatten
      .map(db.run(_).map(_ ⇒ true))
      .head
  }
}
