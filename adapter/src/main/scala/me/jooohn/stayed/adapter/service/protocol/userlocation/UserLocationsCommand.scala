package me.jooohn.stayed.adapter.service.protocol.userlocation
import cats.implicits._
import io.circe.Decoder
import io.circe.generic.semiauto._
import me.jooohn.stayed.adapter.service.protocol.BaseCommand

sealed trait UserLocationsCommand
object UserLocationsCommand {

  case class Create(label: String) extends UserLocationsCommand

  implicit val userLocationsCommandDecoder: Decoder[UserLocationsCommand] =
    BaseCommand.decodeAs(
      "CREATED" -> deriveDecoder[Create].widen[UserLocationsCommand]
    )

}
