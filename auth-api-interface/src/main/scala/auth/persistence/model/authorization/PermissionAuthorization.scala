package auth.persistence.model.authorization

import auth.model.core.User
import com.mohiva.play.silhouette.api.{ Authenticator, Authorization }

trait PermissionAuthorization[A <: Authenticator] extends Authorization[User, A]
