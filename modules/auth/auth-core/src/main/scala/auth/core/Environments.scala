package auth.core

import auth.core.model.core.User
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.{DummyAuthenticator, JWTAuthenticator}

/**
  * Environment used by Silhouette.
  * Specified type of our User class and what Authenticator do we use.
  */
trait DefaultEnv extends Env {
  type I = User
  type A = JWTAuthenticator
}
