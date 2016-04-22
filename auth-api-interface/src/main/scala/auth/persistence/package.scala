package auth

import slick.driver.PostgresDriver

package object persistence {
  type AuthDbProfile = auth.persistence.drivers.PostgresDriver // auth.persistence.drivers.H2Driver
  type SilhouetteLoginInfo = com.mohiva.play.silhouette.api.LoginInfo
  type SilhouettePasswordInfo = com.mohiva.play.silhouette.api.util.PasswordInfo
}
