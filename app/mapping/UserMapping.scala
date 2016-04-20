package mapping

import com.mohiva.play.silhouette.api.LoginInfo
import model.core.User
import model.core.User.State.{Activated, Created, Deactivated}
import model.core.User.UserState
import slick.driver.H2Driver.api._

final case class DbUser(uuid: String, // TODO: to uuid
                      email: String,
                      firstName: String,
                      lastName: String,

                      state: UserState)

object UserMappingTodo {
  // TODO: formats refactor
  implicit val stateMapper = MappedColumnType.base[UserState, String] ({
    case Created => "created"
    case Activated => "activated"
    case Deactivated => "deactivated"
  }, {
    case "created" => Created
    case "activated" => Activated
    case "deactivated" => Deactivated
  })
}

import UserMappingTodo._

sealed class UserMapping(tag: Tag) extends Table[DbUser](tag, "users") {
  def uuid: Rep[String] = column[String]("uuid", O.PrimaryKey)
  def email: Rep[String] = column[String]("email")
  def firstName: Rep[String] = column[String]("firstname")
  def lastName: Rep[String] = column[String]("lastname")
  def state: Rep[UserState] = column[UserState]("state")

  def * = (uuid, email, firstName, lastName, state) <> ((DbUser.apply _).tupled, DbUser.unapply)
}

object UserTable {
  lazy val table = TableQuery[UserMapping]
}

final case class DbLoginInfo(id: Long, providerId: String, providerKey: String)
sealed class LoginInfoMapping(tag: Tag) extends Table[DbLoginInfo](tag, "logininfo") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def providerId: Rep[String] = column[String]("providerid")
  def providerKey: Rep[String] = column[String]("providerKey")

  def * = (id, providerId, providerKey) <> ((DbLoginInfo.apply _).tupled, DbLoginInfo.unapply)
}

object LoginInfoTable {
  lazy val table = TableQuery[LoginInfoMapping]

  def findDbLoginInfo(loginInfo: LoginInfo): Query[LoginInfoMapping, DbLoginInfo, Seq] =
    table.filter(db => db.providerId === loginInfo.providerID && db.providerKey === loginInfo.providerKey)
}


final case class UserToLoginInfo(userUuid: String, loginInfoId: Long)
sealed class UserToLoginInfoMapping(tag: Tag) extends Table[UserToLoginInfo](tag, "user_to_logininfo") {
  def userUuid: Rep[String] = column[String]("user_uuid")
  def loginInfoId: Rep[Long] = column[Long]("logininfo_id")

  foreignKey("fk_user_uuid", userUuid, UserTable.table)(_.uuid)
  foreignKey("fk_logininfo_id", loginInfoId, LoginInfoTable.table)(_.id)

  def * = (userUuid, loginInfoId) <> ((UserToLoginInfo.apply _).tupled, UserToLoginInfo.unapply)
}

object UserToLoginInfoTable {
  lazy val table = TableQuery[UserToLoginInfoMapping]
}

final case class DbPasswordInfo(loginInfoId: Long,
                                hasher: String,
                                password: String,
                                salt: Option[String])
class PasswordInfoMapping(tag: Tag) extends Table[DbPasswordInfo](tag, "passwordinfo") {
  def hasher = column[String]("hasher")
  def password = column[String]("password")
  def salt = column[Option[String]]("salt")
  def loginInfoId = column[Long]("loginInfoId")

  foreignKey("fk_logininfo_id", loginInfoId, LoginInfoTable.table)(_.id)

  def * = (loginInfoId, hasher, password, salt) <> (DbPasswordInfo.tupled, DbPasswordInfo.unapply)
}

object PasswordInfoTable {
  lazy val table = TableQuery[PasswordInfoMapping]
}
