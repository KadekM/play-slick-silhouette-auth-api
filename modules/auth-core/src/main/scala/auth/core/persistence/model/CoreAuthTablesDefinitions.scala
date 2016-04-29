package auth.core.persistence.model

import java.util.UUID

import auth.core.model.core.{Permission, User}
import User.UserState
import auth.core.persistence.{HasAuthDbProfile, SilhouetteLoginInfo}
import slick.lifted.ProvenShape

/**
  * Contains the most important structure for the vary basics of auth
  */
trait CoreAuthTablesDefinitions extends AuthModelMappingSupport with HasAuthDbProfile {
  import driver.api._

  sealed class UserMapping(tag: Tag) extends Table[User](tag, "users") {
    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)
    def email: Rep[String] = column[String]("email")
    def firstName: Rep[String] = column[String]("firstName")
    def lastName: Rep[String] = column[String]("lastName")
    def state: Rep[UserState] = column[UserState]("state")

    def * : ProvenShape[User] =
      (uuid, email, firstName, lastName, state) <> ((User.apply _).tupled, User.unapply)
  }

  val usersQuery = TableQuery[UserMapping]

  sealed class LoginInfoMapping(tag: Tag) extends Table[LoginInfo](tag, "loginInfos") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userUuid: Rep[UUID] = column[UUID]("users_uuid")
    def providerId: Rep[String] = column[String]("providerId")
    def providerKey: Rep[String] = column[String]("providerKey")

    foreignKey("fk_users_uuid", userUuid, usersQuery)(_.uuid)

    def * = (id, userUuid, providerId, providerKey) <> ((LoginInfo.apply _).tupled, LoginInfo.unapply)
  }

  val loginInfosQuery = TableQuery[LoginInfoMapping]

  // TODO: remove
  def findDbLoginInfo(loginInfo: SilhouetteLoginInfo): Query[LoginInfoMapping, LoginInfo, Seq] =
    loginInfosQuery.filter(db ⇒ db.providerId === loginInfo.providerID && db.providerKey === loginInfo.providerKey)

  sealed class PasswordInfoMapping(tag: Tag) extends Table[PasswordInfo](tag, "passwordInfos") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfos_id")

    foreignKey("fk_logininfos_id", loginInfoId, loginInfosQuery)(_.id)

    def * = (loginInfoId, hasher, password, salt) <> (PasswordInfo.tupled, PasswordInfo.unapply)
  }

  val passwordInfosQuery = TableQuery[PasswordInfoMapping]

  sealed class PermissionMapping(tag: Tag) extends Table[Permission](tag, "permissions") {
    def name = column[Permission]("name", O.PrimaryKey)

    def * = name
  }

  val permissionsQuery = TableQuery[PermissionMapping]

  sealed class PermissionToUserMapping(tag: Tag) extends Table[PermissionToUser](tag, "permissionsToUsers") {
    def permission = column[Permission]("permissions_name")
    def userUuid = column[UUID]("users_uuid")

    foreignKey("fk_permissions", permission, permissionsQuery)(_.name)
    foreignKey("fk_users_uuid", userUuid, usersQuery)(_.uuid)
    def * = (permission, userUuid) <> (PermissionToUser.tupled, PermissionToUser.unapply)
  }

  val permissionsToUsersQuery = TableQuery[PermissionToUserMapping]
}
