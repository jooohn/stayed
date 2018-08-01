package me.jooohn.stayed.adapter

import java.time.Instant

import io.circe.{Decoder, Encoder}
import me.jooohn.stayed.infrastructure.{Iso, Prism}
import shapeless.tag.@@

import scala.reflect.runtime.universe.TypeTag

trait PrismForCirce extends LensFeatures {

  def prismEncoder[A, B](implicit E: Encoder[A], P: Prism[A, B]): Encoder[B] =
    Encoder[A].contramap(P.from)

  def prismDecoder[A, B](implicit D: Decoder[A], P: Prism[A, B]): Decoder[B] =
    Decoder[A].emap(a => P.toOpt(a).toRight("Failed to decode "))

  implicit def stringPrismEncoder[A](implicit P: Prism[String, A]): Encoder[A] =
    prismEncoder[String, A]
  implicit def longPrismEncoder[A](implicit P: Prism[Long, A]): Encoder[A] = prismEncoder[Long, A]

  implicit def stringPrismDecoder[A](implicit P: Prism[String, A]): Decoder[A] =
    prismDecoder[String, A]
  implicit def longPrismDecoder[A](implicit P: Prism[Long, A]): Decoder[A] = prismDecoder[Long, A]
}

trait IsoForCirce extends PrismForCirce {

  def isoEncoder[A, B](implicit E: Encoder[A], I: Iso[A, B]): Encoder[B] =
    Encoder[A].contramap(I.from)

  def isoDecoder[A, B](implicit D: Decoder[A], I: Iso[A, B]): Decoder[B] =
    Decoder[A].map(I.to)

  implicit def stringIsoEncoder[A](implicit I: Iso[String, A]): Encoder[A] = isoEncoder[String, A]
  implicit def longIsoEncoder[A](implicit I: Iso[Long, A]): Encoder[A] = isoEncoder[Long, A]

  implicit def stringIsoDecoder[A](implicit I: Iso[String, A]): Decoder[A] = isoDecoder[String, A]
  implicit def longIsoDecoder[A](implicit I: Iso[Long, A]): Decoder[A] = isoDecoder[Long, A]

}

trait CirceFeatures extends IsoForCirce
object CirceFeatures extends CirceFeatures
