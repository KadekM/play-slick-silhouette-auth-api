package auth.direct.module

import akka.actor.ActorSystem
import auth.core.DefaultEnv
import auth.core.module.SilhouetteContext
import auth.direct.service.UserService
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, IDGenerator, PasswordHasher, PasswordInfo}
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

sealed class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())
  }

  @Provides
  def provideEnvironment(
      userService: UserService,
      authenticatorService: AuthenticatorService[DefaultEnv#A],
      eventBus: EventBus,
      @SilhouetteContext ec: ExecutionContext
  ): Environment[DefaultEnv] =
    Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)(ec)

  @Provides
  def provideAuthInfoRepository(
      passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo],
      @SilhouetteContext ec: ExecutionContext
  ): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)(ec)

  @Provides
  def provideCredentialsProvider(
      authInfoRepository: AuthInfoRepository,
      passwordHasher: PasswordHasher,
      @SilhouetteContext ec: ExecutionContext
  ): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))(ec)
}
