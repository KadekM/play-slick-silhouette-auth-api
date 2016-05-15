package auth.api.model.exchange

import auth.core.model.core.Permission
import com.mohiva.play.silhouette.api.util.Credentials

final case class AssignPermission(permission: Permission)

final case class CreatePassword(password: String)

final case class SignIn(identifier: String, password: String, rememberMe: Boolean) {
  lazy val toCredentials: Credentials = Credentials(identifier, password)
}

final case class SignUp(identifier: String, firstName: String, lastName: String)

final case class UpdateUser(
    email: Option[String], firstName: Option[String], lastName: Option[String])
