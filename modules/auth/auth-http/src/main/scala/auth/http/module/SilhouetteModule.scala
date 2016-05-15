package auth.http.module

import auth.core.DefaultEnv
import auth.core.module.SilhouetteContext
import auth.core.service.BasicUserService
import auth.core.service.authorization.PermissionsAuthorizer
import auth.http.service.impl.BasicUserServiceViaHttpImpl
import auth.http.service.impl.authorization.impl.PermissionsViaHttpAuthorizer
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

sealed class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {}

  @Provides
  def providePermissionsAuthorizer(configuration: Configuration, ws: WSClient)(
      implicit ec: ExecutionContext): PermissionsAuthorizer =
    new PermissionsViaHttpAuthorizer(configuration, ws)(ec)

  @Provides
  def provideUserService(configuration: Configuration,
                         wsClient: WSClient,
                         @SilhouetteContext ec: ExecutionContext): BasicUserService =
    new BasicUserServiceViaHttpImpl(configuration, wsClient)(ec)

  @Provides
  def provideEnvironment(userService: BasicUserService,
                         eventBus: EventBus,
                         authenticatorService: AuthenticatorService[DefaultEnv#A],
                         @SilhouetteContext ec: ExecutionContext): Environment[DefaultEnv] =
    Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)(ec)
}
