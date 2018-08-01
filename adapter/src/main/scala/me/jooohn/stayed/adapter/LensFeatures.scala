package me.jooohn.stayed.adapter

import java.time.{Instant, ZoneId}

import me.jooohn.stayed.domain.UserId
import me.jooohn.stayed.infrastructure.{Iso, Prism}
import shapeless.tag
import shapeless.tag.@@

import scala.util.Try

trait LensFeatures {

  implicit val userIdIso: Iso[String, UserId] = Iso.newInstance(_.toString, UserId.apply)

  implicit def idIso[A]: Iso[String, String @@ A] = Iso.newInstance(identity, tag[A][String])

  implicit val zoneIdPrism: Prism[String, ZoneId] =
    Prism.newInstance(_.getId, str => Try(ZoneId.of(str)).toOption)

  implicit val instantIso: Iso[Long, Instant] =
    Iso.newInstance(_.toEpochMilli, Instant.ofEpochMilli)

}
object LensFeatures
