package auth.core.module

import auth.core.model.core.{ AccessAdmin, AccessBar }
import auth.core.persistence.SilhouettePasswordInfo
import auth.core.persistence.model.authorization.PermissionsAuthorizer
import auth.core.persistence.model.authorization.impl.DbPermissionsAuthorizerImpl
import auth.core.persistence.model.dao.impl.{ LoginInfoDaoImpl, PasswordInfoDaoImpl, PermissionDaoImpl, UserDaoImpl }
import auth.core.persistence.model.dao.{ LoginInfoDao, PasswordInfoDao, PermissionDao, UserDao }
import auth.core.persistence.model.{ AuthDatabaseConfigProvider, AuthDbAccess, CoreAuthTablesDefinitions }
import com.google.inject.{ AbstractModule, Inject, Provides }
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.codingwell.scalaguice.ScalaModule
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.backend.DatabaseConfig
import slick.dbio.Effect.Schema
import slick.profile.BasicProfile

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration._

sealed class PersistenceModule extends AbstractModule with ScalaModule with SilhouetteProviders with DaoProviders {
  override def configure(): Unit = {
    bind[InitInMemoryDb].asEagerSingleton // only for in memory db, create tables at start
  }

  /**
    * Provides functionality to avoid spreading NamedDatabase across codebase
    *
    * @return config provider for auth database
    */
  @Provides
  def provideAuthDatabaseConfigProvider(@NamedDatabase("auth") dbConfigProvider: DatabaseConfigProvider): AuthDatabaseConfigProvider =
    new AuthDatabaseConfigProvider {
      override def get[P <: BasicProfile]: DatabaseConfig[P] = dbConfigProvider.get
    }

  @Provides
  def providePermissionsAuthorizer(doa: PermissionDao)(implicit ec: ExecutionContext): PermissionsAuthorizer =
    new DbPermissionsAuthorizerImpl(doa)
}

/**
  * Providers for dao layer
  */
trait DaoProviders {
  // Remark: It is safe to pass default ec (play's one - akka's default dispatcher)
  // since all db actions run on separate Slick's dispatcher, and those required by DAOs are
  // only for mapping and flatmapping over futures
  @Provides def provideUserDao(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): UserDao =
    new UserDaoImpl(db)

  @Provides def provideLoginInfoDao(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): LoginInfoDao =
    new LoginInfoDaoImpl(db)

  @Provides def providePasswordInfoDao(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): PasswordInfoDao =
    new PasswordInfoDaoImpl(db)

  @Provides def providePermissionDao(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): PermissionDao =
    new PermissionDaoImpl(db)
}

/**
  * Providers required by Silhouette
  */
trait SilhouetteProviders {
  @Provides def provideDelegableAuthInfoDaoForPasswordInfo(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): DelegableAuthInfoDAO[SilhouettePasswordInfo] =
    new PasswordInfoDaoImpl(db)
}


// TODO: This is just temporary hack to init db
class InitInMemoryDb @Inject() (protected val dbConfigProvider: AuthDatabaseConfigProvider)
  extends AuthDbAccess with CoreAuthTablesDefinitions {

  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  private val f: DBIOAction[Unit, NoStream, Schema] = for {
    _ ← usersQuery.schema.create
    _ ← loginInfosQuery.schema.create
    _ ← passwordInfosQuery.schema.create
    _ ← permissionsQuery.schema.create
    _ ← permissionsToUsersQuery.schema.create
  } yield ()
  println("Core tables created")

  Await.ready(db.run(f.transactionally), 10.seconds)

  // todo: Populate permissions
  Await.ready(db.run(permissionsQuery += AccessAdmin), 10.seconds)
  Await.ready(db.run(permissionsQuery += AccessBar), 10.seconds)
  println("Permissions populated")

}

