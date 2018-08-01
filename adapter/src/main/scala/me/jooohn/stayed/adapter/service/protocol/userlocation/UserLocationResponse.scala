package me.jooohn.stayed.adapter.service.protocol.userlocation

import me.jooohn.stayed.domain.UserLocation

object UserLocationResponse {

  case class Created(id: UserLocation.Id)

}
