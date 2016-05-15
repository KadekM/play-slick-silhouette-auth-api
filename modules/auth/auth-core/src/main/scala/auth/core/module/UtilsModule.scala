package auth.core.module

import auth.core.util.{CookieAuthFilter, CookieSettings}
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

sealed class UtilsModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[CookieAuthFilter]
    bind[CookieSettings]
  }
}
