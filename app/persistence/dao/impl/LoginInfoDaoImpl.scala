package persistence.dao.impl

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import persistence.{DbLoginInfo, LoginInfoTable}
import persistence.dao.LoginInfoDao
import persistence.drivers.AuthPostgresDriver
import slick.backend.DatabaseConfig

import scala.concurrent.Future

class LoginInfoDaoImpl @Inject() (dbConfig: DatabaseConfig[AuthPostgresDriver]) extends LoginInfoDao {
  import play.api.libs.concurrent.Execution.Implicits._
  import dbConfig.driver.api._
  import dbConfig.driver._

  //override def find(loginInfo: LoginInfo): Future[Option[LoginInfo]] = ???
  override def save(loginInfo: LoginInfo, userUuid: String): Future[Unit] = {
    val act = for {
      _ <- LoginInfoTable.query += DbLoginInfo(-1, userUuid, loginInfo.providerID, loginInfo.providerKey)
    } yield ()

    dbConfig.db.run(act)
  }
}
