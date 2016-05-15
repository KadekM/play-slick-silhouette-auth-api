package auth.direct

package object persistence {
  type AuthDbProfile          = auth.direct.persistence.drivers.PostgresDriver // auth.direct.persistence.drivers.H2Driver
  type SilhouetteLoginInfo    = com.mohiva.play.silhouette.api.LoginInfo
  type SilhouettePasswordInfo = com.mohiva.play.silhouette.api.util.PasswordInfo
}
