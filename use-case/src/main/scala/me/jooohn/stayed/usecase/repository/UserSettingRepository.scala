package me.jooohn.stayed.usecase.repository

import me.jooohn.stayed.domain.{UserId, UserSetting}

trait UserSettingRepository[F[_]] {

  def store(userSetting: UserSetting): F[Unit]

  def resolveBy(userId: UserId): F[Option[UserSetting]]

}
