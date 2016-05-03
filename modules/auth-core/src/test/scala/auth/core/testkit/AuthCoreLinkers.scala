package auth.core.testkit

import auth.core.model.core.User
import auth.core.persistence.model.PermissionToUser

trait AuthCoreLinkers {
  implicit val userPermissionLinker = new Linker[User, PermissionToUser] {
    override def link(r: User, a: PermissionToUser): PermissionToUser =
      a.copy(userUuid = r.uuid)
  }
}
