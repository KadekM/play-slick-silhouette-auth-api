package auth.direct.module

import auth.core.service.authorization.PermissionsAuthorizer
import auth.direct.persistence.model.AuthDatabaseConfigProvider
import auth.direct.service.authorization.impl.DbPermissionsAuthorizerImpl
import auth.direct.service.impl._
import auth.direct.service.{PermissionService, UserService}
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

sealed class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {}

  @Provides
  def provideUserService(dbConfig: AuthDatabaseConfigProvider)(
      implicit ec: ExecutionContext): UserService =
    new UserServiceImpl(dbConfig)

  @Provides
  def providePermissonService(dbConfig: AuthDatabaseConfigProvider)(
      implicit ec: ExecutionContext): PermissionService =
    new PermissionServiceImpl(dbConfig)

  @Provides
  def providePermissionsAuthorizer(service: PermissionService)(
      implicit ec: ExecutionContext): PermissionsAuthorizer =
    new DbPermissionsAuthorizerImpl(service)
}
