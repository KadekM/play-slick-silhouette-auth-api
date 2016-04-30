package auth.api.module

import auth.core.persistence.model.{ AuthDatabaseConfigProvider, AuthDbAccess }
import com.google.inject.{ AbstractModule, Inject, Provides }
import net.codingwell.scalaguice.ScalaModule
import auth.api.persistence.repo.{ Hasher, UserTokenRepo }
import auth.api.persistence.repo.impl.{ Sha1HasherImpl, UserTokenRepoImpl }
import auth.api.persistence.TablesDefinitions

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration._

class PersistenceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    bind[InitInMemoryDb].asEagerSingleton
  }

  @Provides def provideUserTokenRepo(dbConfigProvider: AuthDatabaseConfigProvider,
    hasher: Hasher)(implicit ec: ExecutionContext): UserTokenRepo =
    new UserTokenRepoImpl(dbConfigProvider, hasher)
}

// TODO: temporary hack to init db
class InitInMemoryDb @Inject() (protected val dbConfigProvider: AuthDatabaseConfigProvider)
    extends AuthDbAccess with TablesDefinitions {
  import driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  private val f = for {
    _ ← userTokensQuery.schema.create
  } yield ()

  Await.ready(db.run(f.transactionally), 10.seconds)
  println("Other tables created")
}
