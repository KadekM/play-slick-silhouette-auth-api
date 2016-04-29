package auth.core.persistence.model.dao

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.PermissionToUser

import scala.concurrent.Future

trait PermissionDao {
  /**
    * Grants permission to user with userUuid
 *
    * @return true if new permission was granted successfully, otherwise false
    */
  def grant(permission: Permission, userUuid: UUID): Future[Boolean]


  /**
    * Revokes permission of user with userUuid
 *
    * @return true if new permission was revoked successfully, otherwise false
    */
  def revoke(permission: Permission, userUuid: UUID): Future[Boolean]

  /**
    * Finds permission of specific user with all permission details
 *
    * @return Some of found permission details when they are found, otherwise None
    */
  def find(permission: Permission, userUuid: UUID): Future[Option[PermissionToUser]]


  /**
    * @return list of all possible permissions
    */
  def allPossible(): Future[Seq[Permission]]

  /**
    * @return all permissions that had been assigned to users
    */
  def allAssigned(): Future[Seq[PermissionToUser]]
}
