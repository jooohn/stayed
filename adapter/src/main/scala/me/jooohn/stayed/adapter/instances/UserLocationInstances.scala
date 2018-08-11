package me.jooohn.stayed.adapter.instances
import java.time.Instant

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import me.jooohn.stayed.domain.UserLocation

trait UserLocationInstances {
  import UserLocation._
  import lens._
  import tagged._
  import instant._

  implicit val stateEncoder: Encoder[State] =
    Encoder[Option[Instant]].contramap {
      case State.Entered(at) => Some(at)
      case State.Exited      => None
    }

  implicit val stateDecoder: Decoder[State] = Decoder[Option[Instant]].map {
    case Some(at) => State.Entered(at)
    case None     => State.Exited
  }

  implicit val stayEncoder: Encoder[Stay] =
    deriveEncoder[Stay]

  implicit val stayDecoder: Decoder[Stay] =
    deriveDecoder[Stay]

  implicit val userLocationEncoder: Encoder[UserLocation] = deriveEncoder[UserLocation]

  implicit val userLocationDecoder: Decoder[UserLocation] = deriveDecoder[UserLocation]

}
