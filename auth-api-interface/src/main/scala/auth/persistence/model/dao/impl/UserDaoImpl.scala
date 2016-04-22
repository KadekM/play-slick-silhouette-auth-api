package auth.persistence.model.dao.impl

import auth.model.core.User
import com.mohiva.play.silhouette
import User.UserState
import auth.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import auth.persistence.model.dao.UserDao

import scala.concurrent.Future

// TODO: should not run queries, should only prepare them for services, instead of full dbconfig, get just api
class UserDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)
  extends UserDao with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  import play.api.libs.concurrent.Execution.Implicits._
  println("user dao init")

  override def find(loginInfo: silhouette.api.LoginInfo): Future[Option[User]] = {
    println("finding ", loginInfo)
    val userQuery = for {
    (loginInfo, user) ← findDbLoginInfo(loginInfo)
        .join(usersQuery).on(_.userUuid === _.uuid)
    } yield user

    dbConfig.db.run(userQuery.result.headOption)
  }

  override def find(userUuid: String): Future[Option[User]] = {
    println("finging", userUuid)
    val query = usersQuery.filter(_.uuid === userUuid)
    dbConfig.db.run(query.result.headOption)
  }

  override def save(user: User): Future[User] = {
    println("saving", user)

    val act = for {
      _ ← usersQuery.insertOrUpdate(user)
    } yield ()

    dbConfig.db.run(act).map(_ ⇒ user)
  }

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = {
   val act =  usersQuery
      .filter(_.uuid === userUuid)
      .map(_.state)
      .update(newState)

    dbConfig.db.run(act).map(amountChanged => amountChanged != 0)
  }
}
