package auth.core.model.core

sealed trait Permission

case object AccessAdmin extends Permission
case object AccessBar extends Permission

object Permission {
  def fromString(x: String): Option[Permission] = Array(AccessAdmin, AccessBar).find(_.toString == x)
}
