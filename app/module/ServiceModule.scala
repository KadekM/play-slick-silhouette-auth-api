package module

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import service.{Hasher, RegistrationTokenService, UserService}
import service.impl.{InMemoryRegistrationTokenServiceImpl, InMemoryUserServiceImpl, Sha1HasherImpl}

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    //bind[UserService].to[UserServiceImpl]
    bind[UserService].to[InMemoryUserServiceImpl].asEagerSingleton
    bind[RegistrationTokenService].to[InMemoryRegistrationTokenServiceImpl].asEagerSingleton
  }
}
