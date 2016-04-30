package auth.api.module

import auth.api.persistence.repo.{Hasher, UserTokenRepo}
import auth.api.service.UserTokenService
import auth.api.service.impl.{InMemoryUserTokenServiceImpl, UserTokenServiceImpl}
import auth.core.persistence.model.AuthDatabaseConfigProvider
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

sealed class ServiceModule extends AbstractModule with ScalaModule with ServiceProviders {
  override def configure(): Unit = {
  }
}

trait ServiceProviders {
  @Provides def provideUserTokenService(authDb: AuthDatabaseConfigProvider, userTokenRepo: UserTokenRepo)(implicit ec: ExecutionContext): UserTokenService =
    new UserTokenServiceImpl(authDb, userTokenRepo)
}

sealed class InMemoryUserTokenServiceProvider @Inject() (hasher: Hasher) extends Provider[UserTokenService] {
  override def get(): UserTokenService = new InMemoryUserTokenServiceImpl(hasher)
}
