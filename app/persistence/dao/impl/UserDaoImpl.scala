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
      dbLoginInfo ← LoginInfoTable.findDbLoginInfo(loginInfo)
      dbUserLoginInfo ← UserToLoginInfoTable.query.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser ← UserTable.query.filter(_.uuid === dbUserLoginInfo.userUuid)
    } yield dbUser

    dbConfig.db.run(userQuery.result.headOption).map { dbUserOption ⇒
      dbUserOption.map { user ⇒
        User(user.uuid, loginInfo, user.email, user.firstName, user.lastName, user.state)
      }
    }
  }

  override def save(user: User): Future[User] = {
    val dbUser = DbUser(user.uuid, user.email, user.firstName, user.lastName, user.state)
    val dbLoginInfo = DbLoginInfo(-1, user.loginInfo.providerID, user.loginInfo.providerKey)

    val loginInfoAction = {
      val retrieveLoginInfo = LoginInfoTable.query.filter(
        info ⇒ info.providerId === user.loginInfo.providerID &&
          info.providerKey === user.loginInfo.providerKey).result.headOption

      val insertLoginInfo = LoginInfoTable.query
        .returning(LoginInfoTable.query.map(_.id))
        .into((info, id) ⇒ info.copy(id = id)) += dbLoginInfo

      for {
        loginInfoOpt ← retrieveLoginInfo
        loginInfo ← loginInfoOpt.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield loginInfo
    }

    val actions = for {
      _ ← UserTable.query.insertOrUpdate(dbUser)
      loginInfo ← loginInfoAction
      _ ← UserToLoginInfoTable.query += UserToLoginInfo(dbUser.uuid, loginInfo.id)
    } yield ()

    dbConfig.db.run(actions.transactionally).map(_ ⇒ user)
  }

  override def find(userUuid: String): Future[Option[User]] = {
    val query = for {
      dbUser ← UserTable.query.filter(_.uuid === userUuid)
      dbUserLoginInfo ← UserToLoginInfoTable.query.filter(_.userUuid === dbUser.uuid)
      dbLoginInfo ← LoginInfoTable.query.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)

    dbConfig.db.run(query.result.headOption).map(resultOpt ⇒
      resultOpt.map {
        case (user, loginInfo) ⇒ User(user.uuid, LoginInfo(loginInfo.providerId, loginInfo.providerKey),
          user.email, user.firstName, user.lastName, user.state)
      })
  }

  override def setState(userUuid: String, newState: UserState): Future[Boolean] = {
   val act =  UserTable.query
      .filter(_.uuid === userUuid)
      .map(_.state)
      .update(newState)

    dbConfig.db.run(act).map(amountChanged => amountChanged != 0)
  }
}
