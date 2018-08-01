package me.jooohn.stayed.usecase.repository

import me.jooohn.stayed.domain.{UserId, UserLocation}

trait UserLocationRepository[F[_]] {

  def create(userId: UserId, label: String): F[UserLocation]

  def update(userLocation: UserLocation): F[Unit]

  def resolveBy(userId: UserId, id: UserLocation.Id): F[Option[UserLocation]]

}
