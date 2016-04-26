package auth.persistence.model.dao.impl

import auth.model.core.Permission
import auth.persistence.model.{ AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions, PermissionToUser }
import auth.persistence.model.dao.PermissionDao

import scala.concurrent.Future

class PermissionDaoImpl(protected val dbConfigProvider: AuthDatabaseConfigProvider)
    extends PermissionDao with AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  //TODO
  import scala.concurrent.ExecutionContext.Implicits._

  override def grant(permission: Permission, userUuid: String): Future[Boolean] = {
    val act = permissionsToUsersQuery += PermissionToUser(permission, userUuid)
    db.run(act).map(changed ⇒ changed != 0)
  }

  override def revoke(permission: Permission, userUuid: String): Future[Boolean] = {
    val act = permissionsToUsersQuery
      .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)
      .delete

    db.run(act).map(changed ⇒ changed != 0)
  }

  override def find(permission: Permission, userUuid: String): Future[Option[PermissionToUser]] = {
    val act = permissionsToUsersQuery
      .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)

    db.run(act.result.headOption)
  }

  override def allPossible(): Future[Seq[Permission]] = db.run(permissionsQuery.result)

  override def allAssigned(): Future[Seq[PermissionToUser]] = db.run(permissionsToUsersQuery.result)
}
