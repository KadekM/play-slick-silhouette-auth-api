package auth.persistence

/**
  * Generic trait to describe that we need driver.
  * Could be as well moved to some common library depended by all.
  */
trait HasAuthDbProfile {
  protected val driver: AuthDbProfile
}
