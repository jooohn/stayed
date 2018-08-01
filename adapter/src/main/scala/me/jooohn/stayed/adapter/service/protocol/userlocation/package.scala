package me.jooohn.stayed.adapter.service.protocol

import java.time.Instant

import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.generic.semiauto._
import me.jooohn.stayed.adapter.{CirceFeatures, LensFeatures}
import me.jooohn.stayed.adapter.service.protocol.userlocation.UserLocationResponse.Created
import me.jooohn.stayed.domain.UserLocation.Stay
import shapeless.tag.@@

package object userlocation extends CirceFeatures {
  import UserLocationCommand._

  val createJsonDecoder: Decoder[Create] = deriveDecoder[Create]
  val enterJsonDecoder: Decoder[Enter] = deriveDecoder[Enter]
  val exitJsonDecoder: Decoder[Exit] = deriveDecoder[Exit]

  case class BaseCommand(`type`: String, attributes: Option[Json])
  implicit val decodeBaseCommand: Decoder[BaseCommand] =
    deriveDecoder[BaseCommand]

  implicit val commandDecoder: Decoder[UserLocationCommand] =
    for {
      base <- decodeBaseCommand
      command <- (base match {
        case BaseCommand("CREATE", Some(attributes)) =>
          createJsonDecoder.decodeJson(attributes)
        case BaseCommand("ENTER", Some(attributes)) =>
          enterJsonDecoder.decodeJson(attributes)
        case BaseCommand("EXIT", Some(attributes)) =>
          exitJsonDecoder.decodeJson(attributes)
        case _ => Left(DecodingFailure("Unexpected command", Nil))
      }).fold[Decoder[UserLocationCommand]](Decoder.failed, Decoder.const)
    } yield command

  implicit val createEncoder: Encoder[Created] = deriveEncoder[Created]
  implicit val stayEncoder: Encoder[Stay] =
    deriveEncoder[Stay]
}
