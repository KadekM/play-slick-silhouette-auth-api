package auth.persistence.model.dao.impl

import auth.persistence.SilhouetteLoginInfo
import auth.persistence.model._
import auth.persistence.model.dao.LoginInfoDao
import auth.persistence.model.{AuthDbAccess, LoginInfo}

import scala.concurrent.Future

class LoginInfoDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)
    extends LoginInfoDao with AuthDbAccess with CoreAuthTablesDefinitions {

  println("login info dao initiated")

  import driver.api._
  import play.api.libs.concurrent.Execution.Implicits._

  override def save(loginInfo: SilhouetteLoginInfo, userUuid: String): Future[Unit] = {
    println("saving logininfo")
    val act = for {
      _ ← loginInfosQuery += LoginInfo(-1, userUuid, loginInfo.providerID, loginInfo.providerKey)
    } yield ()

    db.run(act)
  }
}
