package me.jooohn.stayed.adapter.aggregate

import java.time.ZoneId

import cats.Monad
import doobie.implicits._
import doobie.util.composite.Composite
import doobie.util.transactor.Transactor
import me.jooohn.stayed.adapter.DoobieFeatures
import me.jooohn.stayed.domain.{UserId, UserSetting}
import me.jooohn.stayed.usecase.repository.UserSettingRepository

class UserSettingRepositoryForDB[F[_]: Monad](val transactor: Transactor[F])
    extends UserSettingRepository[F]
    with RepositoryForDB[F] {
  import UserSettingRepositoryForDB._

  override def store(userSetting: UserSetting): F[Unit] = transaction {
    sql"""INSERT INTO user_settings
         |  VALUES (${userSetting.userId}, ${userSetting.zoneId})
         |  ON CONFLICT DO UPDATE SET timezone = ${userSetting.zoneId}""".update.run
      .map(_ => ())
  }

  override def resolveBy(userId: UserId): F[Option[UserSetting]] = transaction {
    sql"""SELECT * FROM user_settings WHERE user_id = ${userId}""".query[UserSetting].option
  }

}

object UserSettingRepositoryForDB extends DoobieFeatures {
  implicit val userSettingComposite: Composite[UserSetting] =
    Composite[(UserId, ZoneId)]
      .imap((UserSetting.apply _).tupled)(Function.unlift(UserSetting.unapply))
}
