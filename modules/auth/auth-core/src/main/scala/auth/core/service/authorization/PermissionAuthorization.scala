package auth.core.service.authorization

import auth.core.model.core.User
import com.mohiva.play.silhouette.api.{Authenticator, Authorization}

/**
  * Authorisation for User from our Domain
  */
trait PermissionAuthorization[A <: Authenticator] extends Authorization[User, A]
