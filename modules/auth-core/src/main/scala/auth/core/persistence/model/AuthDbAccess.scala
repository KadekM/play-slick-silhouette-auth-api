package auth.core.persistence.model

import auth.core.persistence.AuthDbProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

trait AuthDbAccess extends HasDatabaseConfigProvider[AuthDbProfile]

trait AuthDatabaseConfigProvider extends DatabaseConfigProvider
