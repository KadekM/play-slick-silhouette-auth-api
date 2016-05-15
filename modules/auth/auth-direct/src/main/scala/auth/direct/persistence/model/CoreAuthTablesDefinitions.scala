package auth.direct.persistence.model

import java.util.UUID

import auth.core.model.core.User.UserState
import auth.core.model.core.{Permission, PermissionToUser, User}
import auth.direct.persistence._
import com.mohiva.play.silhouette
import slick.dbio.Effect.{Read, Write}
import slick.lifted.{ForeignKeyQuery, Index, ProvenShape}

import scala.concurrent.ExecutionContext

/**
  * Contains the most important structure for the vary basics of auth
  */
trait CoreAuthTablesDefinitions extends AuthModelMappingSupport with HasAuthDbProfile {
  import driver.api._

  sealed class UserMapping(tag: Tag) extends Table[User](tag, "users") {
    def uuid: Rep[UUID]        = column[UUID]("uuid", O.PrimaryKey)
    def email: Rep[String]     = column[String]("email")
    def firstName: Rep[String] = column[String]("firstName")
    def lastName: Rep[String]  = column[String]("lastName")
    def state: Rep[UserState]  = column[UserState]("state")

    def * : ProvenShape[User] =
      (uuid, email, firstName, lastName, state) <> ((User.apply _).tupled, User.unapply)
  }

  val usersQuery = TableQuery[UserMapping]

  sealed class LoginInfoMapping(tag: Tag) extends Table[LoginInfo](tag, "loginInfos") {
    def id: Rep[Long]            = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userUuid: Rep[UUID]      = column[UUID]("users_uuid")
    def providerId: Rep[String]  = column[String]("providerId")
    def providerKey: Rep[String] = column[String]("providerKey")

    def usersFk: ForeignKeyQuery[UserMapping, User] =
      foreignKey("fk_users_uuid", userUuid, usersQuery)(_.uuid)

    def * =
      (id, userUuid, providerId, providerKey) <> ((LoginInfo.apply _).tupled, LoginInfo.unapply)
  }

  val loginInfosQuery = TableQuery[LoginInfoMapping]

  sealed class PasswordInfoMapping(tag: Tag) extends Table[PasswordInfo](tag, "passwordInfos") {
    def hasher: Rep[String]       = column[String]("hasher")
    def password: Rep[String]     = column[String]("password")
    def salt: Rep[Option[String]] = column[Option[String]]("salt")
    def loginInfoId: Rep[Long]    = column[Long]("loginInfos_id")

    def loginInfosFk: ForeignKeyQuery[LoginInfoMapping, LoginInfo] =
      foreignKey("fk_logininfos_id", loginInfoId, loginInfosQuery)(_.id)

    def * = (loginInfoId, hasher, password, salt) <> (PasswordInfo.tupled, PasswordInfo.unapply)
  }

  val passwordInfosQuery = TableQuery[PasswordInfoMapping]

  sealed class PermissionMapping(tag: Tag) extends Table[Permission](tag, "permissions") {
    def name: Rep[Permission] = column[Permission]("name", O.PrimaryKey)

    def * = name
  }

  val permissionsQuery = TableQuery[PermissionMapping]

  sealed class PermissionToUserMapping(tag: Tag)
      extends Table[PermissionToUser](tag, "permissionsToUsers") {
    def permission: Rep[Permission] = column[Permission]("permissions_name")
    def userUuid: Rep[UUID]         = column[UUID]("users_uuid")

    def permissionsFk: ForeignKeyQuery[PermissionMapping, Permission] =
      foreignKey("fk_permissions", permission, permissionsQuery)(_.name)

    def usersFk: ForeignKeyQuery[UserMapping, User] =
      foreignKey("fk_users_uuid", userUuid, usersQuery)(_.uuid)

    def idx: Index =
      index("permission_to_user_idx", (permission, userUuid), unique = true)

    def * = (permission, userUuid) <> (PermissionToUser.tupled, PermissionToUser.unapply)
  }

  val permissionsToUsersQuery = TableQuery[PermissionToUserMapping]

  class Api()(implicit ec: ExecutionContext) {
    def findDbLoginInfo(loginInfo: SilhouetteLoginInfo): Query[LoginInfoMapping, LoginInfo, Seq] =
      loginInfosQuery.filter(
          db ⇒ db.providerId === loginInfo.providerID && db.providerKey === loginInfo.providerKey)

    object Users {
      def find(loginInfo: silhouette.api.LoginInfo): DBIOAction[Option[User], NoStream, Read] = {
        val userQuery = for {
          (loginInfo, user) ← findDbLoginInfo(loginInfo).join(usersQuery).on(_.userUuid === _.uuid)
        } yield user

        userQuery.result.headOption
      }

      def find(userUuid: UUID): DBIOAction[Option[User], NoStream, Read] = {
        val query = usersQuery.filter(_.uuid === userUuid)
        query.result.headOption
      }

      def save(user: User): DBIOAction[User, NoStream, Write] = {
        val act = for {
          _ ← usersQuery.insertOrUpdate(user)
        } yield ()

        act.map(_ ⇒ user)
      }

      def setState(userUuid: UUID, newState: UserState): DBIOAction[Boolean, NoStream, Write] = {
        val act = usersQuery.filter(_.uuid === userUuid).map(_.state).update(newState)

        act.map(amountChanged ⇒ amountChanged != 0)
      }
    }

    object Permissions {
      def grant(permission: Permission, userUuid: UUID): DBIOAction[Boolean, NoStream, Write] = {
        val act = permissionsToUsersQuery += PermissionToUser(permission, userUuid)
        act.map(changed ⇒ changed != 0)
      }

      def revoke(permission: Permission, userUuid: UUID): DBIOAction[Boolean, NoStream, Write] = {
        val act = permissionsToUsersQuery
          .filter(x ⇒ x.permission === permission && x.userUuid === userUuid)
          .delete

        act.map(changed ⇒ changed != 0)
      }

      def find(permission: Permission,
               userUuid: UUID): DBIOAction[Option[PermissionToUser], NoStream, Read] = {
        val act = permissionsToUsersQuery.filter(
            x ⇒ x.permission === permission && x.userUuid === userUuid)

        act.result.headOption
      }
    }

    object PermissionsToUsers {
      def allOfUser(userUuid: UUID): DBIOAction[Seq[PermissionToUser], NoStream, Read] =
        permissionsToUsersQuery.filter(_.userUuid === userUuid).result
    }
  }
}
