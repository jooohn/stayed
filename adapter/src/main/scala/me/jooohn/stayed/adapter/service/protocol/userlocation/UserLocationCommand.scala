package me.jooohn.stayed.adapter.service.protocol.userlocation

import java.time.Instant

import io.circe.{Decoder, DecodingFailure}
import io.circe.generic.semiauto._
import me.jooohn.stayed.adapter.service.protocol.BaseCommand
import me.jooohn.stayed.domain.UserLocation

sealed trait UserLocationCommand
object UserLocationCommand {
  import me.jooohn.stayed.adapter.instances.all._

  case class Create(label: String) extends UserLocationCommand
  case class Enter(userLocationId: UserLocation.Id, at: Instant) extends UserLocationCommand
  case class Exit(userLocationId: UserLocation.Id, at: Instant) extends UserLocationCommand

  val createJsonDecoder: Decoder[Create] = deriveDecoder[Create]
  val enterJsonDecoder: Decoder[Enter] = deriveDecoder[Enter]
  val exitJsonDecoder: Decoder[Exit] = deriveDecoder[Exit]

  implicit val commandDecoder: Decoder[UserLocationCommand] =
    for {
      base <- Decoder[BaseCommand]
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
}
