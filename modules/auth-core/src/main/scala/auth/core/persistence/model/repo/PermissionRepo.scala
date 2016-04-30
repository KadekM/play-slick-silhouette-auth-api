package auth.core.persistence.model.repo

import java.util.UUID

import auth.core.model.core.Permission
import auth.core.persistence.model.PermissionToUser
import slick.dbio.Effect.{Read, Write}
import slick.dbio.{DBIOAction, NoStream}
import slick.profile.FixedSqlStreamingAction

trait PermissionRepo {
  /**
    * Grants permission to user with userUuid
 *
    * @return true if new permission was granted successfully, otherwise false
    */
  def grant(permission: Permission, userUuid: UUID): DBIOAction[Boolean, NoStream, Write]


  /**
    * Revokes permission of user with userUuid
 *
    * @return true if new permission was revoked successfully, otherwise false
    */
  def revoke(permission: Permission, userUuid: UUID): DBIOAction[Boolean, NoStream, Write]

  /**
    * Finds permission of specific user with all permission details
    *
    * @return Some of found permission details when they are found, otherwise None
    */
  def find(permission: Permission, userUuid: UUID): DBIOAction[Option[PermissionToUser], NoStream, Read]


  /**
    * @return list of all possible permissions
    */
  def allPossible: FixedSqlStreamingAction[Seq[Permission], Permission, Read]

  /**
    * @return all permissions that had been assigned to users
    */
  def allAssigned: FixedSqlStreamingAction[Seq[PermissionToUser], PermissionToUser, Read]
}
