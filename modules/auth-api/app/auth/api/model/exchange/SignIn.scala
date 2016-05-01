package auth.api.model.exchange

import com.mohiva.play.silhouette.api.util.Credentials

final case class SignIn(identifier: String, password: String, rememberMe: Boolean) {
  lazy val toCredentials: Credentials = Credentials(identifier, password)
}
