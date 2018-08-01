package me.jooohn.stayed.usecase.repository

import cats.Functor
import me.jooohn.stayed.domain.{UserId, UserSetting}

trait UserSettingRepository[F[_]] {

  def store(userSetting: UserSetting): F[Unit]

  def resolveBy(userId: UserId): F[Option[UserSetting]]

  def resolveOrDefault(userId: UserId)(implicit F: Functor[F]): F[UserSetting] =
    F.map(resolveBy(userId))(_.getOrElse(UserSetting.default(userId)))

}
