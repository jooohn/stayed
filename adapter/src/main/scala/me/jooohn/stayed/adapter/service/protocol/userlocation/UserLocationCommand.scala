package me.jooohn.stayed.adapter.service.protocol.userlocation

import java.time.Instant

import cats.implicits._
import io.circe.Decoder
import io.circe.generic.semiauto._
import me.jooohn.stayed.adapter.service.protocol.BaseCommand

sealed trait UserLocationCommand
object UserLocationCommand {
  import me.jooohn.stayed.adapter.instances.all._

  case class Enter(at: Instant) extends UserLocationCommand
  case class Exit(at: Instant) extends UserLocationCommand

  implicit val userLocationCommandDecoder: Decoder[UserLocationCommand] =
    BaseCommand.decodeAs(
      "ENTERED" -> deriveDecoder[Enter].widen[UserLocationCommand],
      "EXITED" -> deriveDecoder[Exit].widen[UserLocationCommand]
    )
}
