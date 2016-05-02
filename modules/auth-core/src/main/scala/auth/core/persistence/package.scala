package auth.core

import slick.driver.{JdbcProfile, PostgresDriver}

package object persistence {
  type AuthDbProfile = auth.core.persistence.drivers.PostgresDriver // auth.core.persistence.drivers.H2Driver
  type SilhouetteLoginInfo = com.mohiva.play.silhouette.api.LoginInfo
  type SilhouettePasswordInfo = com.mohiva.play.silhouette.api.util.PasswordInfo
}
