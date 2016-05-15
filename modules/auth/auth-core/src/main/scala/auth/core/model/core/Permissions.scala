package auth.core.model.core

sealed trait Permission

object Permission {
  case object AccessAdmin extends Permission
  case object AccessBar   extends Permission

  val allPermissions = List[Permission](AccessAdmin, AccessBar)
  def fromString(x: String): Option[Permission] =
    allPermissions.find(_.toString == x)
}
