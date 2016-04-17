package service

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import model.User

import scala.concurrent.Future

/**
  * UserService provides means to retreive User
  */

trait UserService extends IdentityService[User]

class UserServiceImpl extends UserService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = ???
}
