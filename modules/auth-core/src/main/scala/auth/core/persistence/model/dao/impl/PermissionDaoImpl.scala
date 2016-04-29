package auth.core.persistence.model.dao.impl

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions, PermissionToUser}
import auth.core.persistence.model.dao.PermissionDao

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of permission dao
  * @param ec - execution context only for maps and flatMaps, of futures - it's safe to pass default one
  */
class PermissionDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends PermissionDao with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._

  override def grant(permission: Permission, userUuid: UUID): Future[Boolean] = {
    val act = permissionsToUsersQuery += PermissionToUser(permission, userUuid)
    db.run(act).map(changed ⇒ changed != 0)
  }

  override def revoke(permission: Permission, userUuid: UUID): Future[Boolean] = {
    val act = permissionsToUsersQuery
      .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)
      .delete

    db.run(act).map(changed ⇒ changed != 0)
  }

  override def find(permission: Permission, userUuid: UUID): Future[Option[PermissionToUser]] = {
    val act = permissionsToUsersQuery
      .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)

    db.run(act.result.headOption)
  }

  override def allPossible(): Future[Seq[Permission]] = db.run(permissionsQuery.result)

  override def allAssigned(): Future[Seq[PermissionToUser]] = db.run(permissionsToUsersQuery.result)
}
