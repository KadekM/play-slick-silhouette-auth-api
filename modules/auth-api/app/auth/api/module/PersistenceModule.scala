package auth.api.module

import auth.core.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess}
import com.google.inject.{AbstractModule, Inject, Provides}
import net.codingwell.scalaguice.ScalaModule
import auth.api.model.TablesDefinitions
import auth.api.model.dao.{Hasher, UserTokenDao}
import auth.api.model.dao.impl.{Sha1HasherImpl, UserTokenDaoImpl}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class PersistenceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    bind[InitInMemoryDb].asEagerSingleton
  }

  @Provides def provideUserTokenDao(dbConfigProvider: AuthDatabaseConfigProvider,
                                    hasher: Hasher)(implicit ec: ExecutionContext): UserTokenDao =
    new UserTokenDaoImpl(dbConfigProvider, hasher)
}

// TODO: temporary hack to init db
class InitInMemoryDb @Inject() (protected val dbConfigProvider: AuthDatabaseConfigProvider)
  extends AuthDbAccess with TablesDefinitions {
  import driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  private val f = for {
    _ <- userTokensQuery.schema.create
  } yield ()

  Await.ready(db.run(f.transactionally), 10.seconds)
  println("Other tables created")
}
