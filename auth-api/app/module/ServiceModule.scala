package module

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import net.codingwell.scalaguice.ScalaModule
import persistence.model.dao.{Hasher, UserTokenDao}
import service.UserTokenService
import service.impl.{InMemoryUserTokenServiceImpl, UserTokenServiceImpl}
import utils.SetCookieFilter

sealed class ServiceModule extends AbstractModule with ScalaModule with ServiceProviders {
  override def configure(): Unit = {
    //bind[UserTokenService].toProvider[InMemoryUserTokenServiceProvider].asEagerSingleton
    //todo:
    bind[SetCookieFilter]
  }
}

trait ServiceProviders {
  @Provides def provideUserTokenService(userTokenDao: UserTokenDao): UserTokenService =
    new UserTokenServiceImpl(userTokenDao)
}

sealed class InMemoryUserTokenServiceProvider @Inject() (hasher: Hasher) extends Provider[UserTokenService] {
  override def get(): UserTokenService = new InMemoryUserTokenServiceImpl(hasher)
}
