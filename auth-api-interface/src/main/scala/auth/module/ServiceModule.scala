package auth.module

import auth.persistence.model.dao.{PermissionDao, UserDao}
import auth.service.impl._
import auth.service.{PermissionService, UserService}
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
  }

  @Provides def provideUserService(userDao: UserDao): UserService =
    new UserServiceImpl(userDao)

  @Provides def providePermissionService(permDao: PermissionDao): PermissionService =
    new PermissionServiceImpl(permDao)
}
