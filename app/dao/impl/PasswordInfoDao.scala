package dao.impl

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import mapping.{ DbPasswordInfo, LoginInfoTable, PasswordInfoTable }
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

// todo: is acutally servis not dao/repo
class PasswordInfoDao @Inject() (dbConfig: DatabaseConfig[JdbcProfile]) extends DelegableAuthInfoDAO[PasswordInfo] {

  import play.api.libs.concurrent.Execution.Implicits._
  import dbConfig.driver.api._

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val query = passwordInfoQuery(loginInfo).result.headOption
    dbConfig.db.run(query).map { dbPasswordInfoOpt ⇒
      dbPasswordInfoOpt.map(dbPasswordInfo ⇒
        PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
    }
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    dbConfig.db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  override def remove(loginInfo: LoginInfo): Future[Unit] =
    dbConfig.db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = LoginInfoTable.findDbLoginInfo(loginInfo).joinLeft(PasswordInfoTable.table)
        .on(_.id === _.loginInfoId)

    // todo addorupdate slick?
    val action = query.result.head.flatMap {
      case (dbLoginInfo, Some(dbPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (dbLoginInfo, None) => addAction(loginInfo, authInfo)
    }

    dbConfig.db.run(action).map(_ => authInfo)
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    dbConfig.db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  protected def passwordInfoQuery(loginInfo: LoginInfo) = for {
    dbLoginInfo ← LoginInfoTable.findDbLoginInfo(loginInfo)
    dbPasswordInfo ← PasswordInfoTable.table if dbPasswordInfo.loginInfoId === dbLoginInfo.id
  } yield dbPasswordInfo

  protected def passwordInfoSubQuery(loginInfo: LoginInfo) =
    PasswordInfoTable.table.filter(_.loginInfoId in LoginInfoTable.findDbLoginInfo(loginInfo).map(_.id))

  protected def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    LoginInfoTable.findDbLoginInfo(loginInfo).result.head.flatMap { dbLoginInfo ⇒
      PasswordInfoTable.table +=
        DbPasswordInfo(dbLoginInfo.id, authInfo.hasher, authInfo.password, authInfo.salt)
    }

  private def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    passwordInfoSubQuery(loginInfo)
      .map(dbPasswordInfo ⇒ (dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
      .update((authInfo.hasher, authInfo.password, authInfo.salt))
}
