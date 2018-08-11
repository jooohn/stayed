package me.jooohn.stayed.usecase
import cats.implicits._
import cats.Monad
import me.jooohn.stayed.domain.{ApiToken, UserId, UserSetting}
import me.jooohn.stayed.usecase.repository.{ApiTokenGenerator, UserSettingRepository}

class UserSettingUseCase[M[_]: Monad](
    userSettingRepository: UserSettingRepository[M],
    apiTokenGenerator: ApiTokenGenerator[M]
) {

  def resolveOrCreate(userId: UserId): M[UserSetting] = {
    val user = User(userId)
    for {
      userOpt <- user.resolveSetting
      user <- userOpt.fold(user.createNewSetting)(Monad[M].pure)
    } yield user
  }

  case class User(userId: UserId) {

    def resolveSetting: M[Option[UserSetting]] = userSettingRepository.resolveBy(userId)

    def createNewSetting: M[UserSetting] =
      for {
        apiToken <- apiTokenGenerator.generate
        userSetting <- saveSettingWith(apiToken)
      } yield userSetting

    def saveSettingWith(apiToken: ApiToken): M[UserSetting] = {
      val newSetting = UserSetting(userId, apiToken)
      Monad[M].map(userSettingRepository.store(newSetting))(_ => newSetting)
    }

  }

}
