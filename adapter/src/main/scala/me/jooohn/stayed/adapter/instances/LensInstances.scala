package me.jooohn.stayed.adapter.instances
import doobie.util.meta.Meta
import io.circe.{Decoder, Encoder}
import me.jooohn.stayed.infrastructure.{Iso, Prism}

import scala.reflect.runtime.universe.TypeTag

trait LensInstances extends IsoInstances

trait IsoInstances extends PrismInstances {

  def isoMeta[A: Meta, B: TypeTag](implicit I: Iso[A, B]): Meta[B] =
    Meta[A].xmap(I.to, I.from)

  implicit def stringIsoMeta[A: TypeTag](implicit I: Iso[String, A]): Meta[A] = isoMeta[String, A]

  implicit def longIsoMeta[A: TypeTag](implicit I: Iso[Long, A]): Meta[A] = isoMeta[Long, A]

  def isoEncoder[A: Encoder, B](implicit I: Iso[A, B]): Encoder[B] =
    Encoder[A].contramap(I.from)

  implicit def stringIsoEncoder[A: TypeTag](implicit I: Iso[String, A]): Encoder[A] =
    isoEncoder[String, A]

  implicit def longIsoEncoder[A: TypeTag](implicit I: Iso[Long, A]): Encoder[A] =
    isoEncoder[Long, A]

  implicit def isoDecoder[A: Decoder, B](implicit I: Iso[A, B]): Decoder[B] =
    Decoder[A].map(I.to)

  implicit def stringIsoDecoder[A: TypeTag](implicit I: Iso[String, A]): Decoder[A] =
    isoDecoder[String, A]

  implicit def longIsoDecoder[A: TypeTag](implicit I: Iso[Long, A]): Decoder[A] =
    isoDecoder[Long, A]

}

trait PrismInstances {

  def prismMetaUnsafe[A: Meta, B: TypeTag](implicit P: Prism[A, B]): Meta[B] =
    Meta[A].xmap(a => P.toOpt(a).getOrElse(throw new IllegalArgumentException()), P.from)

  implicit def stringPrismMetaUnsafe[A: TypeTag](implicit P: Prism[String, A]): Meta[A] =
    prismMetaUnsafe[String, A]

  implicit def longPrismMetaUnsafe[A: TypeTag](implicit P: Prism[Long, A]): Meta[A] =
    prismMetaUnsafe[Long, A]

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
