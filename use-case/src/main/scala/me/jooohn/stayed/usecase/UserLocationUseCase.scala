package me.jooohn.stayed.usecase

import java.time.Instant

import cats.Monad
import cats.data.EitherT
import me.jooohn.stayed.domain.{DomainError, UserId, UserLocation}
import me.jooohn.stayed.usecase.repository._

class UserLocationUseCase[M[_]: Monad](
    userLocationRepository: UserLocationRepository[M],
    userSettingRepository: UserSettingRepository[M]) {
  import UserLocationUseCase._
  import cats.syntax.either._

  type ErrorOr[A] = EitherT[M, Error, A]

  def resolve(userId: UserId, userLocationId: UserLocation.Id): ErrorOr[UserLocation] =
    User(userId) resolveUserLocationBy userLocationId

  def create(userId: UserId, label: String): M[UserLocation] =
    User(userId).createUserLocation(label)

  def enter(userId: UserId, userLocationId: UserLocation.Id, at: Instant): ErrorOr[Unit] =
    User(userId).enter(userLocationId, at)

  def exit(userId: UserId, userLocationId: UserLocation.Id, at: Instant): ErrorOr[Unit] =
    User(userId).exit(userLocationId, at)

  case class User(userId: UserId) {

    def resolveUserLocationBy(userLocationId: UserLocation.Id): ErrorOr[UserLocation] =
      for {
        userLocationOpt <- EitherT.right(userLocationRepository.resolveBy(userId, userLocationId))
        userLocation <- EitherT.fromOption(userLocationOpt, NotFound(userLocationId): Error)
      } yield userLocation

    def createUserLocation(label: String): M[UserLocation] =
      userLocationRepository.create(userId, label)

    def enter(userLocationId: UserLocation.Id, at: Instant): ErrorOr[Unit] =
      modifyUserLocation(userLocationId)(_.entered(at))

    def exit(userLocationId: UserLocation.Id, at: Instant): ErrorOr[Unit] =
      for {
        userSetting <- EitherT.right(userSettingRepository.resolveOrDefault(userId))
        _ <- modifyUserLocation(userLocationId)(_.exited(at, userSetting.zoneId))
      } yield ()

    private[this] def modifyUserLocation(userLocationId: UserLocation.Id)(
        f: UserLocation => Either[DomainError, UserLocation]): ErrorOr[Unit] =
      for {
        userLocation <- resolveUserLocationBy(userLocationId)
        modified <- EitherT.fromEither(f(userLocation).leftMap(FromDomainError))
        _ <- storeUserLocation(modified)
      } yield ()

    private[this] def storeUserLocation(userLocation: UserLocation): ErrorOr[Unit] =
      EitherT.right(userLocationRepository.update(userLocation))

  }
}

object UserLocationUseCase {
  sealed abstract class Error(val message: String)
  case class NotFound(userLocationId: UserLocation.Id)
      extends Error(
        s"Could not find user location with id: $userLocationId"
      )
  case class FromDomainError(error: DomainError) extends Error(error.message)
}
