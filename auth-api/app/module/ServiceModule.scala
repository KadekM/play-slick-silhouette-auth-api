package module

import auth.DefaultEnv
import auth.persistence.model.dao.LoginInfoDao
import auth.service.UserService
import com.google.inject.{AbstractModule, Inject, Provider, Provides}
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.{SignInCredentialsController, SignUpController, VerifyController}
import net.codingwell.scalaguice.ScalaModule
import persistence.model.dao.{Hasher, UserTokenDao}
import persistence.model.dao.impl.Sha1HasherImpl
import play.api.i18n.MessagesApi
import service.UserTokenService
import service.impl.{InMemoryUserTokenServiceImpl, UserTokenServiceImpl}

sealed class ServiceModule extends AbstractModule with ScalaModule with ControllerProviders with ServiceProviders {
  override def configure(): Unit = {
    //bind[UserTokenService].toProvider[InMemoryUserTokenServiceProvider].asEagerSingleton
  }
}

trait ServiceProviders {
  @Provides def provideUserTokenService(userTokenDao: UserTokenDao): UserTokenService =
    new UserTokenServiceImpl(userTokenDao)
}

sealed class InMemoryUserTokenServiceProvider @Inject()(hasher: Hasher) extends Provider[UserTokenService] {
  override def get(): UserTokenService = new InMemoryUserTokenServiceImpl(hasher)
}

trait ControllerProviders {
  @Provides def provideSignInCredentialsController(silhouette: Silhouette[DefaultEnv],
    translate: MessagesApi,
    userService: UserService,
    credentialsProvider: CredentialsProvider): SignInCredentialsController = new SignInCredentialsController(silhouette, translate, userService, credentialsProvider)

  @Provides def provideSignUpController(silhouette: Silhouette[DefaultEnv],
    passwordHasher: PasswordHasher,
    translate: MessagesApi,
    userService: UserService,
    userTokenService: UserTokenService,
    loginInfoDao: LoginInfoDao,
    authInfoRepository: AuthInfoRepository): SignUpController = new SignUpController(silhouette, passwordHasher, translate, userService, userTokenService, loginInfoDao, authInfoRepository)

  @Provides def provideVerifyController(silhouette: Silhouette[DefaultEnv]): VerifyController = new VerifyController(silhouette)
}
