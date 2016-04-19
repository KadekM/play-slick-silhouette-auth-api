package service

import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule
import play.api.db.slick.DatabaseConfigProvider
import service.impl._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Hasher].to[Sha1HasherImpl]

    bind[UserService].to[UserServiceImpl]
    bind[UserTokenService].to[InMemoryUserTokenServiceImpl].asEagerSingleton
  }
}
