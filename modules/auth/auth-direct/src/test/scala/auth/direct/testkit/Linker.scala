package auth.direct.testkit

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

