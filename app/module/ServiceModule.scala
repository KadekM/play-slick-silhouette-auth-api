package module

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import service.{Hasher, UserTokenService, UserService}
import service.impl.{InMemoryUserTokenServiceImpl, InMemoryUserServiceImpl, Sha1HasherImpl}

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    //bind[UserService].to[UserServiceImpl]
    bind[UserService].to[InMemoryUserServiceImpl].asEagerSingleton
    bind[UserTokenService].to[InMemoryUserTokenServiceImpl].asEagerSingleton
  }
}
