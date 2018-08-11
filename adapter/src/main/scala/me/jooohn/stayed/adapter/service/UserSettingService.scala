package me.jooohn.stayed.adapter.service
import cats.Monad
import cats.effect.Sync
import cats.implicits._
import me.jooohn.stayed.domain.UserAccount
import me.jooohn.stayed.usecase.UserSettingUseCase
import org.http4s.AuthedService

class UserSettingService[F[_]: Monad: Sync](userSettingUseCase: UserSettingUseCase[F])
    extends BaseService[F] {
  import me.jooohn.stayed.adapter.instances.all._

  val service: AuthedService[UserAccount, F] = AuthedService[UserAccount, F] {

    case GET -> Root / "user_setting" as userAccount =>
      for {
        userSetting <- userSettingUseCase.resolveOrCreate(userAccount.userId)
        response <- Ok(userSetting)
      } yield response

  }
}
