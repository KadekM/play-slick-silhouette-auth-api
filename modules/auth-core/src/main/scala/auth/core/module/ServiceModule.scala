package auth.core.module

import auth.core.persistence.model.dao.{PermissionDao, UserDao}
import auth.core.service.impl._
import auth.core.service.{PermissionService, UserService}
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
