package auth.module

import auth.persistence._
import auth.persistence.model.dao.impl.{LoginInfoDaoImpl, PasswordInfoDaoImpl, UserDaoImpl}
import auth.persistence.model.dao.{LoginInfoDao, PasswordInfoDao, UserDao}
import auth.persistence.model.{AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions}
import com.google.inject.{AbstractModule, Inject, Provides}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.codingwell.scalaguice.ScalaModule
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.backend.DatabaseConfig
import slick.dbio.Effect.Schema
import slick.profile.BasicProfile

import scala.concurrent.Await
import scala.concurrent.duration._

sealed class PersistenceModule extends AbstractModule with ScalaModule with SilhouetteProviders with DaoProviders {
  override def configure(): Unit = {
    bind[InitInMemoryDb].asEagerSingleton // only for in memory db, create tables at start
  }

  /**
    * Provides functionality to avoid spreading NamedDatabase across codebase
    * @return config provider for auth database
    */
  @Provides
  def provideAuthDatabaseConfigProvider(@NamedDatabase("auth") dbConfigProvider: DatabaseConfigProvider): AuthDatabaseConfigProvider =
    new AuthDatabaseConfigProvider {
      override def get[P <: BasicProfile]: DatabaseConfig[P] = dbConfigProvider.get
    }
}

class InitInMemoryDb @Inject() (protected val dbConfigProvider: AuthDatabaseConfigProvider)
  extends AuthDbAccess with CoreAuthTablesDefinitions {
  import driver.api._

  // todo
  import scala.concurrent.ExecutionContext.Implicits.global

  private val f: DBIOAction[Unit, NoStream, Schema] = for {
    _ ← usersQuery.schema.create
    _ ← loginInfosQuery.schema.create
    _ ← passwordInfosQuery.schema.create
  } yield ()

  Await.ready(db.run(f.transactionally), 10.seconds)
  println("Core tables created")
}

/**
  * Providers required by Silhouette
  */
trait SilhouetteProviders {
  @Provides def provideDelegableAuthInfoDaoForPasswordInfo(db: AuthDatabaseConfigProvider): DelegableAuthInfoDAO[SilhouettePasswordInfo] =
    new PasswordInfoDaoImpl(db)
}

/**
  * Providers for dao layer
  */
trait DaoProviders {
  @Provides def provideUserDao(db: AuthDatabaseConfigProvider): UserDao =
    new UserDaoImpl(db)

  @Provides def provideLoginInfoDao(db: AuthDatabaseConfigProvider): LoginInfoDao =
    new LoginInfoDaoImpl(db)

  @Provides def providePasswordInfoDao(db: AuthDatabaseConfigProvider): PasswordInfoDao =
    new PasswordInfoDaoImpl(db)
}