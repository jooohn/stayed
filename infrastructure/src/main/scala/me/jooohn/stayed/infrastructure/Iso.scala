package me.jooohn.stayed.infrastructure

trait Iso[A, B] extends Prism[A, B] {

  def to(a: A): B

  override def toOpt(a: A): Option[B] = Some(to(a))

}
object Iso {

  def apply[A, B](implicit I: Iso[A, B]): Iso[A, B] = I

  def newInstance[A, B](f: B => A, t: A => B): Iso[A, B] =
    new Iso[A, B] {
      override def from(b: B): A = f(b)
      override def to(a: A): B = t(a)
    }

}
