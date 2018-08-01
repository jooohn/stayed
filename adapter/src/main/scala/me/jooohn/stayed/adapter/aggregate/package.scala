package me.jooohn.stayed.adapter

import cats.implicits._
import doobie.util.meta.Meta
import io.circe._
import io.circe.jawn._
import io.circe.syntax._
import me.jooohn.stayed.domain.UserLocation
import org.postgresql.util.PGobject
import shapeless.tag.{@@, Tagged}
import shapeless.{Coproduct, Generic, tag}

import scala.reflect.runtime.universe.TypeTag

package object aggregate extends CirceFeatures {

  implicit def encodeAdt[A, Repr <: Coproduct](
      implicit
      gen: Generic.Aux[A, Repr],
      encodeRepr: Encoder[Repr]): Encoder[A] =
    encodeRepr.contramap(gen.to)

  implicit def decodeAdt[A, Repr <: Coproduct](
      implicit
      gen: Generic.Aux[A, Repr],
      decodeRepr: Decoder[Repr]): Decoder[A] =
    decodeRepr.map(gen.from)

  def codedMeta[A: Encoder: Decoder: TypeTag]: Meta[A] =
    Meta[Json].xmap[A](
      _.as[A].fold[A](throw _, identity),
      _.asJson
    )

  implicit val JsonMeta: Meta[Json] =
    Meta
      .other[PGobject]("json")
      .xmap[Json](
        a => parse(a.getValue).leftMap[Json](e => throw e).merge,
        a => {
          val o = new PGobject
          o.setType("json")
          o.setValue(a.noSpaces)
          o
        }
      )

  implicit def taggedMeta: Meta[String @@ UserLocation] = Meta[String].xmap(
    tag[UserLocation][String],
    identity
  )

}
