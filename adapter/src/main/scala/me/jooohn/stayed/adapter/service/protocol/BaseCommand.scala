package me.jooohn.stayed.adapter.service.protocol
import io.circe.generic.semiauto._
import io.circe.{Decoder, DecodingFailure, Json}

case class BaseCommand(
    `type`: String,
    token: Option[String],
    attributes: Option[Json]
)

object BaseCommand {
  implicit val decodeBaseCommand: Decoder[BaseCommand] = deriveDecoder[BaseCommand]

  def decodeAs[A](entries: (String, Decoder[A])*): Decoder[A] =
    decodeAs(Function.unlift(entries.toMap.lift))

  def decodeAs[A](f: PartialFunction[String, Decoder[A]]): Decoder[A] =
    for {
      base <- decodeBaseCommand
      decoded <- f.lift(base.`type`).fold(failed[A]) { decoder =>
        decoder
          .decodeJson(base.attributes.getOrElse(Json.Null))
          .fold(
            Decoder.failed,
            Decoder.const
          )
      }
    } yield decoded

  private def failed[A]: Decoder[A] =
    Decoder.failed(DecodingFailure("Incompatible command", Nil))

}
