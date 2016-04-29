package auth.core.persistence.model.dao.impl

import java.util.UUID

import auth.core.persistence._
import auth.core.persistence.model._
import auth.core.persistence.model.dao.LoginInfoDao
import auth.core.persistence.model.{ AuthDbAccess, LoginInfo }

import scala.concurrent.{ ExecutionContext, Future }

/**
  * Implementation of login info dao
  * @param ec - execution context only for maps and flatMaps, of futures - it's safe to pass default one
  */
class LoginInfoDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends LoginInfoDao with AuthDbAccess with CoreAuthTablesDefinitions {

  println("login info dao initiated")

  import driver.api._

  override def save(loginInfo: SilhouetteLoginInfo, userUuid: UUID): Future[Unit] = {
    println("saving logininfo")
    db.run(loginInfosQuery += LoginInfo(-1, userUuid, loginInfo.providerID, loginInfo.providerKey))
      .map(_ ⇒ ())
  }
}
