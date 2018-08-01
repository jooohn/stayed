package me.jooohn.stayed.adapter.service.protocol.userlocation

import java.time.Instant

import me.jooohn.stayed.domain.UserLocation

sealed trait UserLocationCommand
object UserLocationCommand {
  case class Create(label: String) extends UserLocationCommand
  case class Enter(userLocationId: UserLocation.Id, at: Instant) extends UserLocationCommand
  case class Exit(userLocationId: UserLocation.Id, at: Instant) extends UserLocationCommand
}
