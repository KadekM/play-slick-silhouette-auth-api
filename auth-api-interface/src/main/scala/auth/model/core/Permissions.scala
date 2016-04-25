package auth.model.core

sealed trait Permission

case object AccessAdmin extends Permission
case object AccessSpringBar extends Permission
