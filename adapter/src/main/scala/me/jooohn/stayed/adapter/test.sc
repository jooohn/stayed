import java.time.Instant

import io.circe.Encoder
import io.circe.generic.semiauto._
import shapeless.tag.@@

case class MyInstant(value: Instant, id: String @@ MyInstant)

trait Iso[A, B] {
  def encode(a: B): A
}
object Iso {
  def apply[A, B](implicit I: Iso[A, B]): Iso[A, B] = I
}

trait IsoInstances {

  implicit val instantIso: Iso[Long, Instant] = _.toEpochMilli

  implicit def idIso[A]: Iso[String, String @@ A] = _.toString

}

trait IsoEncoder extends IsoInstances {

  implicit def isoLongEncoder[B](implicit I: Iso[Long, B]): Encoder[B] =
    Encoder[Long].contramap(I.encode)
  implicit def isoStringEncoder[B](implicit I: Iso[String, B]): Encoder[B] =
    Encoder[String].contramap(I.encode)

}

object MyInstant extends IsoInstances with IsoEncoder {

  def main(): Unit = {
//    println(deriveEncoder[Instant])
    println(deriveEncoder[MyInstant])
  }

}
MyInstant.main()
