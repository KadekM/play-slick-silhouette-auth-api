package module

import auth.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess}
import com.google.inject.{AbstractModule, Inject, Provides}
import net.codingwell.scalaguice.ScalaModule
import persistence.model.TablesDefinitions
import persistence.model.dao.{Hasher, UserTokenDao}
import persistence.model.dao.impl.{Sha1HasherImpl, UserTokenDaoImpl}

import scala.concurrent.Await
import scala.concurrent.duration._

class PersistenceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    bind[InitInMemoryDb].asEagerSingleton
  }

  @Provides def provideUserTokenDao(dbConfigProvider: AuthDatabaseConfigProvider,
                                    hasher: Hasher): UserTokenDao =
    new UserTokenDaoImpl(dbConfigProvider, hasher)
}

class InitInMemoryDb @Inject() (protected val dbConfigProvider: AuthDatabaseConfigProvider)
  extends AuthDbAccess with TablesDefinitions {
  import driver.api._

  // todo
  import scala.concurrent.ExecutionContext.Implicits.global

  private val f = for {
    _ <- userTokensQuery.schema.create
  } yield ()

  Await.ready(db.run(f.transactionally), 10.seconds)
  println("Other tables created")
}
