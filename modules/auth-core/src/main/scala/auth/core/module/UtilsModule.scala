package auth.core.module

import auth.core.utils.CookieAuthFilter
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

sealed class UtilsModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[CookieAuthFilter]
  }
}
