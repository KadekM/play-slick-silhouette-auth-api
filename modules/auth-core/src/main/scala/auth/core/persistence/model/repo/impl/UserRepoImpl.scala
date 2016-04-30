package auth.core.persistence.model.repo.impl

import java.util.UUID

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import auth.core.persistence.model.repo.UserRepo
import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import com.mohiva.play.silhouette
import slick.dbio.Effect.{Read, Write}

import scala.concurrent.ExecutionContext

/**
  * Provides implementation of user repo
  *
  * @param ec - execution context only for maps and flatMaps, of futures - it's safe to pass default one
  */
class UserRepoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext) extends UserRepo with AuthDbAccess with CoreAuthTablesDefinitions {
  import driver.api._
  import driver.profile._
  println("user repo init")

  override def find(loginInfo: silhouette.api.LoginInfo): DBIOAction[Option[User], NoStream, Read] = {
    println("finding ", loginInfo)
    val userQuery = for {
      (loginInfo, user) ← findDbLoginInfo(loginInfo)
        .join(usersQuery).on(_.userUuid === _.uuid)
    } yield user

    userQuery.result.headOption
  }

  override def find(userUuid: UUID): DBIOAction[Option[User], NoStream, Read] = {
    println("finging", userUuid)
    val query = usersQuery.filter(_.uuid === userUuid)
    query.result.headOption
  }

  override def save(user: User): DBIOAction[User, NoStream, Write] = {
    println("saving", user)

    val act = for {
      _ ← usersQuery.insertOrUpdate(user)
    } yield ()

    act.map(_ ⇒ user)
  }

  override def setState(userUuid: UUID, newState: UserState): DBIOAction[Boolean, NoStream, Write] = {
    val act = usersQuery
      .filter(_.uuid === userUuid)
      .map(_.state)
      .update(newState)

    act.map(amountChanged ⇒ amountChanged != 0)
  }

  override def list: StreamingDriverAction[Seq[User], User, Read] = usersQuery.result
}
