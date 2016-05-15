package auth.direct.testkit

import auth.core.model.core.{PermissionToUser, User}

trait AuthCoreLinkers {
  implicit val userPermissionLinker = new Linker[User, PermissionToUser] {
    override def link(r: User, a: PermissionToUser): PermissionToUser =
      a.copy(userUuid = r.uuid)
  }
}
