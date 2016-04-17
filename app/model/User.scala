package model
import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

final case class User(uuid: UUID,
                      loginInfo: LoginInfo,
                      email: String,
                      firstName: String,
                      lastName: String) extends Identity

object User {
  def default: User = User(UUID.randomUUID, LoginInfo("",""), "", "", "")
}
