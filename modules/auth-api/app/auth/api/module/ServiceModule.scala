package auth.api.module

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import auth.core.utils.CookieAuthFilter
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import net.codingwell.scalaguice.ScalaModule
import auth.api.model.dao.{Hasher, UserTokenDao}
import auth.api.service.UserTokenService
import auth.api.service.impl.{InMemoryUserTokenServiceImpl, UserTokenServiceImpl}

sealed class ServiceModule extends AbstractModule with ScalaModule with ServiceProviders {
  override def configure(): Unit = {
    //bind[UserTokenService].toProvider[InMemoryUserTokenServiceProvider].asEagerSingleton
    //todo:
    bind[CookieAuthFilter]
  }
}

trait ServiceProviders {
  @Provides def provideUserTokenService(userTokenDao: UserTokenDao): UserTokenService =
    new UserTokenServiceImpl(userTokenDao)
}

sealed class InMemoryUserTokenServiceProvider @Inject() (hasher: Hasher) extends Provider[UserTokenService] {
  override def get(): UserTokenService = new InMemoryUserTokenServiceImpl(hasher)
}
