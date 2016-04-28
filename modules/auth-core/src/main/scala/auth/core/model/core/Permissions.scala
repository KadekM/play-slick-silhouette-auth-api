package auth.core.model.core

sealed trait Permission

case object AccessAdmin extends Permission
case object AccessBar extends Permission

object Permission {
  val accessAdmin = "access_admin"
  val accessBar = "access_bar"
}
