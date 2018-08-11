package me.jooohn.stayed.adapter.aggregate

import cats.Monad
import doobie.implicits._
import doobie.util.composite.Composite
import doobie.util.transactor.Transactor
import me.jooohn.stayed.adapter.instances.UserSettingInstances
import me.jooohn.stayed.domain.{ApiToken, UserId, UserSetting}
import me.jooohn.stayed.usecase.repository.UserSettingRepository

class UserSettingRepositoryForDB[F[_]: Monad](val transactor: Transactor[F])
    extends UserSettingRepository[F]
    with RepositoryForDB[F] {
  import UserSettingRepositoryForDB._

  override def store(userSetting: UserSetting): F[Unit] = transaction {
    sql"""INSERT INTO user_settings
            VALUES (${userSetting.userId}, ${userSetting.apiToken})
            ON CONFLICT (user_id) DO UPDATE SET api_token = ${userSetting.apiToken}""".update.run
      .map(_ => ())
  }

  override def resolveBy(userId: UserId): F[Option[UserSetting]] = transaction {
    sql"""SELECT * FROM user_settings WHERE user_id = ${userId}""".query[UserSetting].option
  }

}

object UserSettingRepositoryForDB extends UserSettingInstances {
  implicit val userSettingComposite: Composite[UserSetting] =
    Composite[(UserId, ApiToken)]
      .imap((UserSetting.apply _).tupled)(Function.unlift(UserSetting.unapply))
}
