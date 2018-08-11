package me.jooohn.stayed.usercase.repository

import java.util.UUID

import me.jooohn.stayed.domain.{UserId, UserLocation}
import me.jooohn.stayed.usecase.repository.UserLocationRepository

import scala.collection.mutable
import scala.util.Try

class InMemoryUserLocationRepository(
    val map: mutable.Map[UserLocation.Id, UserLocation] = mutable.Map.empty)
    extends UserLocationRepository[Try] {
  val store = new InMemoryStore[UserLocation.Id, UserLocation](map)(_.id)

  override def findAllBy(userId: UserId): Try[List[UserLocation]] = Try {
    map.values.filter(_.userId == userId).toList
  }

  override def create(userId: UserId, label: String): Try[UserLocation] = {
    val id = UserLocation.Id(UUID.randomUUID().toString)
    val userLocation = UserLocation.create(id, userId, label)
    store.insert(userLocation) map (_ => userLocation)
  }

  override def update(userLocation: UserLocation): Try[Unit] = store.update(userLocation)

  override def resolveBy(userId: UserId, id: UserLocation.Id): Try[Option[UserLocation]] =
    store.find(id).map(_.filter(_.userId == userId))
}
