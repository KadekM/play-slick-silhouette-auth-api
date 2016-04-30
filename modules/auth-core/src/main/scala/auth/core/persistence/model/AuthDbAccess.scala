package auth.core.persistence.model

import auth.core.persistence.AuthDbProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

/**
  * Mix in this driver if you are using dependency injection and require
  * Auth database with it's profile, i.e. Useful if you require only
  * single database
  * {{{
  * class MyService @Inject()(protected val dbConfigProvider: AuthDatabaseConfigProvider)
  *   extends AuthDbAccess {
  *     import driver.api._
  *   }
  * }}}
  */
trait AuthDbAccess extends HasDatabaseConfigProvider[AuthDbProfile]

/**
  * Provider that gets injected, i.e.
  * {{{
  * class MyService @Inject()(authDb: AuthDatabaseConfigProvider) {
  *   val (db, driver) = { val x = authDb.get[AuthDbProfile]; (x.db, x.driver) }
  * }
  * }}}
  * You can use it to get multiple different DBs in single instance
  */
trait AuthDatabaseConfigProvider extends DatabaseConfigProvider
