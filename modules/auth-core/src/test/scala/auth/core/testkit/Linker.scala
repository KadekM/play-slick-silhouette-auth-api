package auth.core.testkit

import auth.core.model.core.User

/**
  * Links instances to parent
  */
trait Linker[R, A] {
  /**
    * Links instance `a` to instance `r`
    *
    * @return linked instance
    */
  def link(r: R, a: A): A
}



