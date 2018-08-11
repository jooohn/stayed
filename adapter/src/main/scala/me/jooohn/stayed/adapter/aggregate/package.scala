package me.jooohn.stayed.adapter

import doobie.util.meta.Meta
import io.circe._
import io.circe.syntax._

import scala.reflect.runtime.universe.TypeTag

package object aggregate {
  import instances.json._

  def codedMeta[A: Encoder: Decoder: TypeTag]: Meta[A] =
    Meta[Json].xmap[A](
      _.as[A].fold[A](throw _, identity),
      _.asJson
    )

}
