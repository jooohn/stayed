package me.jooohn.stayed.adapter.service

import cats.Applicative
import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import me.jooohn.stayed.adapter.CirceFeatures
import me.jooohn.stayed.adapter.CirceFeatures
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder}

trait BaseService[F[_]] extends Http4sDsl[F] with CirceFeatures {

  implicit def circeEntityDecoder[A: Decoder](implicit F: Sync[F]): EntityDecoder[F, A] =
    jsonOf[F, A]

  implicit def circeStringEncoder(implicit A: Applicative[F]): EntityEncoder[F, String] =
    EntityEncoder.stringEncoder[F]
  implicit def circeEntityEncoder[A: Encoder](implicit F: Sync[F]): EntityEncoder[F, A] =
    jsonEncoder[F].contramap(Encoder[A].apply)

}
