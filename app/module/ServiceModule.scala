package module

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import service.{Hasher, UserService, UserTokenService}
import service.impl._

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    bind[InitDb].asEagerSingleton
    bind[UserService].to[UserServiceImpl]
    bind[UserTokenService].to[UserTokenServiceImpl]
  }
}

class InitDb {
  println("creating db")
}

