package auth.core.module

import auth.core.persistence.model.AuthDatabaseConfigProvider
import auth.core.persistence.model.repo.{PermissionRepo, UserRepo}
import auth.core.service.authorization.PermissionsAuthorizer
import auth.core.service.authorization.impl.DbPermissionsAuthorizerImpl
import auth.core.service.impl._
import auth.core.service.{PermissionService, UserService}
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
  }

  @Provides def provideUserService(dbConfig: AuthDatabaseConfigProvider, userRepo: UserRepo): UserService =
    new UserServiceImpl(dbConfig, userRepo)

  @Provides def providePermissonService(dbConfig: AuthDatabaseConfigProvider, permissionRepo: PermissionRepo): PermissionService =
    new PermissionServiceImpl(dbConfig, permissionRepo)

  @Provides
  def providePermissionsAuthorizer(service: PermissionService)(implicit ec: ExecutionContext): PermissionsAuthorizer =
    new DbPermissionsAuthorizerImpl(service)
}

