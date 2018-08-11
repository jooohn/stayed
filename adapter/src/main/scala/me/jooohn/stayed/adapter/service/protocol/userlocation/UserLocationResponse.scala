package me.jooohn.stayed.adapter.service.protocol.userlocation

import io.circe.Encoder
import io.circe.generic.semiauto._
import me.jooohn.stayed.domain.UserLocation

object UserLocationResponse {
  import me.jooohn.stayed.adapter.instances.all._

  case class Created(id: UserLocation.Id)
  implicit val createdEncoder: Encoder[Created] = deriveEncoder[Created]

}
