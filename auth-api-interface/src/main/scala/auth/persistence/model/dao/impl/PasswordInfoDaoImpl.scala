package auth.persistence.model.dao.impl

import auth.persistence.{SilhouetteLoginInfo, SilhouettePasswordInfo}
import auth.persistence.model._
import auth.persistence.model.dao.PasswordInfoDao

import scala.concurrent.Future

class PasswordInfoDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)
  extends PasswordInfoDao with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  import play.api.libs.concurrent.Execution.Implicits._
  println("passwordinfod daao initi")

  override def find(loginInfo: SilhouetteLoginInfo): Future[Option[SilhouettePasswordInfo]] = {
    val query = passwordInfoQuery(loginInfo).result.headOption
    db.run(query).map { dbPasswordInfoOpt ⇒
      dbPasswordInfoOpt.map(dbPasswordInfo ⇒
        com.mohiva.play.silhouette.api.util.PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
    }
  }

  override def update(loginInfo: SilhouetteLoginInfo, authInfo: SilhouettePasswordInfo): Future[SilhouettePasswordInfo] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  override def remove(loginInfo: SilhouetteLoginInfo): Future[Unit] =
    db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())

  override def save(loginInfo: SilhouetteLoginInfo, authInfo: SilhouettePasswordInfo): Future[SilhouettePasswordInfo] = {
    val query = findDbLoginInfo(loginInfo).joinLeft(passwordInfosQuery)
        .on(_.id === _.loginInfoId)

    val action = query.result.head.flatMap {
      case (dbLoginInfo, Some(dbPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (dbLoginInfo, None) => addAction(loginInfo, authInfo)
    }

    db.run(action).map(_ => authInfo)
  }

  override def add(loginInfo: SilhouetteLoginInfo, authInfo: SilhouettePasswordInfo): Future[SilhouettePasswordInfo] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  protected def passwordInfoQuery(loginInfo: SilhouetteLoginInfo) = for {
    dbLoginInfo ← findDbLoginInfo(loginInfo)
    dbPasswordInfo ← passwordInfosQuery if dbPasswordInfo.loginInfoId === dbLoginInfo.id
  } yield dbPasswordInfo

  protected def passwordInfoSubQuery(loginInfo: SilhouetteLoginInfo) =
    passwordInfosQuery.filter(_.loginInfoId in findDbLoginInfo(loginInfo).map(_.id))

  protected def addAction(loginInfo: SilhouetteLoginInfo, authInfo: SilhouettePasswordInfo) =
    findDbLoginInfo(loginInfo).result.head.flatMap { dbLoginInfo ⇒
      passwordInfosQuery +=
        PasswordInfo(dbLoginInfo.id, authInfo.hasher, authInfo.password, authInfo.salt)
    }

  private def updateAction(loginInfo: SilhouetteLoginInfo, authInfo: SilhouettePasswordInfo) =
    passwordInfoSubQuery(loginInfo)
      .map(dbPasswordInfo ⇒ (dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
      .update((authInfo.hasher, authInfo.password, authInfo.salt))
}
