package testkit

import auth.api.model.core.UserToken
import auth.core.model.core.User
import auth.core.testkit.Linker

trait ModelLinkers {
  implicit val userTokenLinker = new Linker[User, UserToken] {
    override def link(r: User, a: UserToken): UserToken = a.copy(userUuid = r.uuid)
  }
}

