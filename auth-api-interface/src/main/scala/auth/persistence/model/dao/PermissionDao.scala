package auth.persistence.model.dao

import auth.model.core.Permission
import auth.persistence.model.PermissionToUser

import scala.concurrent.Future

trait PermissionDao {
  /**
    * Grants permission to user with userUuid
    * @return true if new permission was granted successfully, otherwise false
    */
  def grant(permission: Permission, userUuid: String): Future[Boolean]


  /**
    * Revokes permission of user with userUuid
    * @return true if new permission was revoked successfully, otherwise false
    */
  def revoke(permission: Permission, userUuid: String): Future[Boolean]


  /**
    * Finds permission of specific user with all permission details
    * @return Some of found permission details when they are found, otherwise None
    */
  def find(permission: Permission, userUuid: String): Future[Option[PermissionToUser]]
}
