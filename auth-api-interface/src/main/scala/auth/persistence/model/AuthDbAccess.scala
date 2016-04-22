package auth.persistence.model

import auth.persistence.AuthDbProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

trait AuthDbAccess extends HasDatabaseConfigProvider[AuthDbProfile]

trait AuthDatabaseConfigProvider extends DatabaseConfigProvider
