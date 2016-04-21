package persistence.dao.impl

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import model.core.User
import model.core.User.UserState
import persistence._
import persistence.dao.UserDao
import persistence.drivers.AuthPostgresDriver
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

// TODO: should not run queries, should only prepare them for services, instead of full dbconfig, get just api
class UserDaoImpl @Inject() (dbConfig: DatabaseConfig[AuthPostgresDriver]) extends UserDao { // Todo: AuthPostgresDriver == JdbcProfile

  import play.api.libs.concurrent.Execution.Implicits._
  import dbConfig.driver.api._
  import dbConfig.driver._


  override def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val userQuery = for {
    (loginInfo, user) ← LoginInfoTable.findDbLoginInfo(loginInfo)
        .join(UserTable.query).on(_.userUuid === _.uuid)
    } yield user

    dbConfig.db.run(userQuery.result.headOption).map { dbUserOption ⇒
      dbUserOption.map { user ⇒
        User(user.uuid, user.email, user.firstName, user.lastName, user.state)
      }
    }
  }

  override def find(userUuid: String): Future[Option[User]] = {
    val query = UserTable.query.filter(_.uuid === userUuid)
    dbConfig.db.run(query.result.headOption)
      .map { opt =>
        opt.map { u =>
          User(u.uuid, u.email, u.firstName, u.lastName, u.state)
        }
      }
  }

  override def save(user: User): Future[User] = {
    val dbUser = DbUser(user.uuid, user.email, user.firstName, user.lastName, user.state)

    val act = for {
      _ ← UserTable.query.insertOrUpdate(dbUser)
    } yield ()

    dbConfig.db.run(act).map(_ ⇒ user)
  }

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = {
   val act =  UserTable.query
      .filter(_.uuid === userUuid)
      .map(_.state)
      .update(newState)

    dbConfig.db.run(act).map(amountChanged => amountChanged != 0)
  }
}
