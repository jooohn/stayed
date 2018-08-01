package me.jooohn.stayed.usercase.repository

import me.jooohn.stayed.domain.{UserId, UserSetting}
import me.jooohn.stayed.usecase.repository.UserSettingRepository

import scala.collection.mutable
import scala.util.Try

class InMemoryUserSettingRepository(map: mutable.Map[UserId, UserSetting] = mutable.Map.empty)
    extends UserSettingRepository[Try] {
  val inMemoryStore = new InMemoryStore(map)(_.userId)

  override def store(userSetting: UserSetting): Try[Unit] =
    inMemoryStore.insert(userSetting)

  override def resolveBy(userId: UserId): Try[Option[UserSetting]] =
    inMemoryStore.find(userId)
}
