package auth.api.module

import auth.api.service._
import auth.api.service.impl.{AuthServiceImpl, ExtendedUserServiceImpl, Sha1HasherImpl, UserTokenServiceImpl}
import auth.core.DefaultEnv
import auth.direct.persistence.model.AuthDatabaseConfigProvider
import auth.direct.persistence.model.dao.LoginInfoDao
import auth.direct.service.UserService
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

sealed class ServiceModule extends AbstractModule with ScalaModule with ServiceProviders {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]
  }
}

trait ServiceProviders {
  @Provides
  def provideUserTokenService(dbConfig: AuthDatabaseConfigProvider, hasher: Hasher)(
      implicit ec: ExecutionContext): UserTokenService =
    new UserTokenServiceImpl(dbConfig, hasher)

  @Provides
  def provideExtendedUserService(dbConfig: AuthDatabaseConfigProvider)(
      implicit ec: ExecutionContext): ExtendedUserService =
    new ExtendedUserServiceImpl(dbConfig)

  @Provides
  def provideExtendedUserService(
      dbConfig: AuthDatabaseConfigProvider,
      userService: UserService,
      silhouette: Silhouette[DefaultEnv],
      credentialsProvider: CredentialsProvider,
      tokenService: UserTokenService,
      loginInfoDao: LoginInfoDao)(implicit ec: ExecutionContext): AuthService =
    new AuthServiceImpl(
        dbConfig, silhouette, credentialsProvider, userService, tokenService, loginInfoDao)
}
