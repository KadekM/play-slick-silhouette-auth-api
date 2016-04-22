package auth.module

import auth.persistence.model.dao.UserDao
import auth.service.impl._
import auth.service.UserService
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
  }

  @Provides def provideUserService(userDao: UserDao): UserService =
    new UserServiceImpl(userDao)
}
