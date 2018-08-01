package me.jooohn.stayed.infrastructure

trait Prism[A, B] {

  def toOpt(a: A): Option[B]

  def from(b: B): A

}
object Prism {

  def apply[A, B](implicit P: Prism[A, B]): Prism[A, B] = P

  def newInstance[A, B](f: B => A, t: A => Option[B]): Prism[A, B] =
    new Prism[A, B] {
      override def from(b: B): A = f(b)
      override def toOpt(a: A): Option[B] = t(a)
    }

}
