package mapping

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.H2Driver.api._

/*
sealed class LoginInfoMapping(tag: Tag) extends Table[LoginInfo](tag, "logininfo") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def providerId = column[String]("providerId")
  def providerKey = column[String]("providerKey")

  override def * = (id.?, providerId, providerKey) <>
}
*/