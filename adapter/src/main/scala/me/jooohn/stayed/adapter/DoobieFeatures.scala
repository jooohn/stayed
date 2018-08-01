package me.jooohn.stayed.adapter

import doobie.util.meta.Meta
import me.jooohn.stayed.infrastructure.{Iso, Prism}

import scala.reflect.runtime.universe.TypeTag

trait PrismForDoobie {

  implicit def prismMetaUnsafe[A: Meta, B: TypeTag](implicit P: Prism[A, B]): Meta[B] =
    Meta[A].xmap(a => P.toOpt(a).getOrElse(throw new IllegalArgumentException()), P.from)

  implicit def stringPrismMeta[A: TypeTag](implicit P: Prism[String, A]): Meta[A] =
    prismMetaUnsafe[String, A]
  implicit def longPrismMeta[A: TypeTag](implicit P: Prism[Long, A]): Meta[A] =
    prismMetaUnsafe[Long, A]

}

trait IsoForDoobie extends PrismForDoobie {

  implicit def isoMeta[A: Meta, B: TypeTag](implicit I: Iso[A, B]): Meta[B] =
    Meta[A].xmap(I.to, I.from)

  implicit def stringIsoMeta[A: TypeTag](implicit I: Iso[String, A]): Meta[A] = isoMeta[String, A]
  implicit def longIsoMeta[A: TypeTag](implicit I: Iso[Long, A]): Meta[A] = isoMeta[Long, A]
}

trait DoobieFeatures extends IsoForDoobie
object DoobieFeatures extends DoobieFeatures
