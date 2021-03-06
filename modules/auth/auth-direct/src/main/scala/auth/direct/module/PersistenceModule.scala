package auth.direct.module

import auth.direct.persistence.SilhouettePasswordInfo
import auth.direct.persistence.model.AuthDatabaseConfigProvider
import auth.direct.persistence.model.dao.impl.{LoginInfoDaoImpl, PasswordInfoDaoImpl}
import auth.direct.persistence.model.dao.{LoginInfoDao, PasswordInfoDao}
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.codingwell.scalaguice.ScalaModule
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.backend.DatabaseConfig
import slick.profile.BasicProfile

import scala.concurrent.ExecutionContext

sealed class PersistenceModule
    extends AbstractModule with ScalaModule with SilhouetteProviders with DaoProviders {
  override def configure(): Unit = {}

  /**
    * Provides functionality to avoid spreading NamedDatabase across codebase
    *
    * @return config provider for auth database
    */
  @Provides
  def provideAuthDatabaseConfigProvider(
      @NamedDatabase("auth") dbConfigProvider: DatabaseConfigProvider)
    : AuthDatabaseConfigProvider =
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
  @Provides
  def provideLoginInfoDao(db: AuthDatabaseConfigProvider)(
      implicit ec: ExecutionContext): LoginInfoDao =
    new LoginInfoDaoImpl(db)

  @Provides
  def providePasswordInfoDao(db: AuthDatabaseConfigProvider)(
      implicit ec: ExecutionContext): PasswordInfoDao =
    new PasswordInfoDaoImpl(db)
}

/**
  * Providers required by Silhouette
  */
trait SilhouetteProviders {
  @Provides
  def provideDelegableAuthInfoDaoForPasswordInfo(db: AuthDatabaseConfigProvider)(
      implicit ec: ExecutionContext): DelegableAuthInfoDAO[SilhouettePasswordInfo] =
    new PasswordInfoDaoImpl(db)
}
