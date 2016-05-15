package auth.core.service

import auth.core.model.core.User
import com.mohiva.play.silhouette.api.services.IdentityService

/**
  * Basic user identity service
  */
trait BasicUserService extends IdentityService[User]
