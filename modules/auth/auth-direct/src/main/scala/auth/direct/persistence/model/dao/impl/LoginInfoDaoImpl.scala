package auth.direct.persistence.model.dao.impl

import java.util.UUID

import auth.direct.persistence._
import auth.direct.persistence.model._
import auth.direct.persistence.model.dao.LoginInfoDao
import auth.direct.persistence.model.{AuthDbAccess, LoginInfo}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of login info dao
  *
  * @param ec - execution context only for maps and flatMaps, of futures - it's safe to pass default one
  */
class LoginInfoDaoImpl(
    protected val dbConfigProvider: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends LoginInfoDao with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._

  override def save(loginInfo: SilhouetteLoginInfo, userUuid: UUID): Future[Unit] =
    db.run(loginInfosQuery += LoginInfo(-1, userUuid, loginInfo.providerID, loginInfo.providerKey))
      .map(_ ⇒ ())
}
