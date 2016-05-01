package auth.core.module

import auth.core.persistence.SilhouettePasswordInfo
import auth.core.persistence.model.AuthDatabaseConfigProvider
import auth.core.persistence.model.dao.impl.{LoginInfoDaoImpl, PasswordInfoDaoImpl}
import auth.core.persistence.model.dao.{LoginInfoDao, PasswordInfoDao}
import auth.core.persistence.model.repo.impl.{PermissionRepoImpl, UserRepoImpl}
import auth.core.persistence.model.repo.{PermissionRepo, UserRepo}
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.codingwell.scalaguice.ScalaModule
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.backend.DatabaseConfig
import slick.profile.BasicProfile

import scala.concurrent.ExecutionContext

sealed class PersistenceModule extends AbstractModule
    with ScalaModule with SilhouetteProviders with DaoProviders with RepoProviders {
  override def configure(): Unit = {
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
}

/**
  * Providers for dao layer
  */
trait DaoProviders {
  // Remark: It is safe to pass default ec (play's one - akka's default dispatcher)
  // since all db actions run on separate Slick's dispatcher, and those required by DAOs are
  // only for mapping and flatmapping over futures
  @Provides def provideLoginInfoDao(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): LoginInfoDao =
    new LoginInfoDaoImpl(db)

  @Provides def providePasswordInfoDao(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): PasswordInfoDao =
    new PasswordInfoDaoImpl(db)
}

trait RepoProviders {
  @Provides def provideUserRepo(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): UserRepo =
    new UserRepoImpl(db)

  @Provides def providePermissionRepo(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): PermissionRepo =
    new PermissionRepoImpl(db)
}

/**
  * Providers required by Silhouette
  */
trait SilhouetteProviders {
  @Provides def provideDelegableAuthInfoDaoForPasswordInfo(db: AuthDatabaseConfigProvider)(implicit ec: ExecutionContext): DelegableAuthInfoDAO[SilhouettePasswordInfo] =
    new PasswordInfoDaoImpl(db)
}

