package mapping

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import model.core.User
import slick.driver.H2Driver.api._

sealed class UserMapping(tag: Tag) extends Table[User](tag, "users") {
  def uuid: Rep[String] = column[String]("uuid", O.PrimaryKey)

  def * = ???
}

sealed class LoginInfoMapping(tag: Tag) extends Table[LoginInfo](tag, "logininfo") {
  def * = ???
}


final case class UserToLoginInfo(userUuid: String, loginInfoId: Long)
sealed class UserToLoginInfoMapping(tag: Tag) extends Table[UserToLoginInfo](tag, "user_to_logininfo") {
  def * = ???
}

final case class PasswordInfo(loginInfoId: Long,
                                hasher: String,
                                password: String,
                                salt: Option[String])
class PasswordInfoMapping(tag: Tag) extends Table[PasswordInfo](tag, "passwordinfo") {
  def * = ???
}
