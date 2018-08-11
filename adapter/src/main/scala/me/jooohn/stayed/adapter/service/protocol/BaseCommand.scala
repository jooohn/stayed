package me.jooohn.stayed.adapter.service.protocol
import io.circe.{Decoder, Json}
import io.circe.generic.semiauto._

case class BaseCommand(`type`: String, attributes: Option[Json])

object BaseCommand {
  implicit val decodeBaseCommand: Decoder[BaseCommand] = deriveDecoder[BaseCommand]
}
