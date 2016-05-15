package testkit

import auth.api.model.core.UserToken
import auth.core.model.core.{PermissionToUser, User}
import auth.direct.testkit.Linker

trait ModelLinkers {
  implicit val userTokenLinker = new Linker[User, UserToken] {
    override def link(r: User, a: UserToken): UserToken = a.copy(userUuid = r.uuid)
  }

  implicit val userPermissionToUserLinker = new Linker[User, PermissionToUser] {
    override def link(r: User, a: PermissionToUser): PermissionToUser = a.copy(userUuid = r.uuid)
  }
}

