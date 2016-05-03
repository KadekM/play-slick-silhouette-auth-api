package testkit.util

import auth.api.persistence.TablesDefinitions
import auth.core.persistence.AuthDbProfile
import auth.core.persistence.model.{ AuthDatabaseConfigProvider, AuthDbAccess }
import com.google.inject.Inject
import org.scalatestplus.play.OneAppPerSuite
import play.api.db.DBApi

/**
  * Various methods to access database and driver
  */
trait TestAuthDatabaseAccess { this: OneAppPerSuite ⇒
  lazy val playAuthDb = app.injector.instanceOf[DBApi].database("auth")

  lazy val tables = app.injector.instanceOf[DbTables]

  lazy val db = tables.database
  lazy val driver = tables.databaseDriver
}

/**
  * Instance containing all possible queries
  *
  * If you are missing mapping capabilities, import tables, i.e:
  * {{{
  * import tables._
  * // Slick would not know how to map over UserState
  * tables.usersQuery.map(_.state).result.headOption
  * }}}
  */
class DbTables @Inject() (protected val dbConfigProvider: AuthDatabaseConfigProvider)
    extends AuthDbAccess with TablesDefinitions {

  private val authDbProfile = dbConfigProvider.get[AuthDbProfile]
  val database = authDbProfile.db
  val databaseDriver = authDbProfile.driver
}

