package auth.api.module

import auth.api.persistence.TablesDefinitions
import auth.direct.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import com.google.inject.{AbstractModule, Inject}
import net.codingwell.scalaguice.ScalaModule

class PersistenceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    //bind[PrintStatementsToConsole].asEagerSingleton
  }
}

// Only to help generate initial evolution
class PrintStatementsToConsole @Inject()(
    protected val dbConfigProvider: AuthDatabaseConfigProvider)
    extends AuthDbAccess with CoreAuthTablesDefinitions with TablesDefinitions {

  import driver.api._

  val tables = List(usersQuery,
                    userTokensQuery,
                    loginInfosQuery,
                    passwordInfosQuery,
                    permissionsQuery,
                    permissionsToUsersQuery)

  println("# --- !Ups")
  val up = tables.map(_.schema.createStatements).reduceLeft(_ ++ _)
  println()
  println("# --- !Downs")
  val down = tables.map(_.schema.dropStatements).reduceLeft(_ ++ _)

  println(up.mkString(";\n"))
  println(down.mkString(";\n"))
}
