package external

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.{AuthInfoRepository, AuthenticatorRepository}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.{PlayCacheLayer, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.{CacheAuthenticatorRepository, DelegableAuthInfoRepository}
import net.codingwell.scalaguice.ScalaModule
import persistence.dao.impl.PasswordInfoDaoImpl
import service._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag // TODO

sealed class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]

    bind[CacheLayer].to[PlayCacheLayer]
    bind[AuthenticatorRepository[JWTAuthenticator]].to[CacheAuthenticatorRepository[JWTAuthenticator]] // todo: swap for db one // todo fix hardcoded jwt?
    // bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO] // todo: storing in db

    bind[Clock].toInstance(Clock())
    bind[EventBus].toInstance(EventBus())
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher())

    bind[ClassTag[JWTAuthenticator]].toInstance(implicitly[ClassTag[JWTAuthenticator]])
  }

  @Provides
  def provideEnvironment(userService: UserService,
    authenticatorService: AuthenticatorService[DefaultEnv#A],
    eventBus: EventBus): Environment[DefaultEnv] =
    Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)

  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)

  @Provides
  def provideAuthenticatorService(idGen: IDGenerator,
    repository: AuthenticatorRepository[JWTAuthenticator],
    clock: Clock): AuthenticatorService[JWTAuthenticator] =
    //todo: load from config
    new JWTAuthenticatorService(
      settings = JWTAuthenticatorSettings(sharedSecret = "a34o5o5n43n34onio43diofgjodfggodngspodp"),
      repository = Some(repository),
      idGenerator = idGen,
      clock = clock)

  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
    passwordHasher: PasswordHasher): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))

}

