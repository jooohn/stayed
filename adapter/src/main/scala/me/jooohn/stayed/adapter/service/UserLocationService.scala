package me.jooohn.stayed.adapter.service

import cats.Monad
import cats.data.EitherT
import cats.effect.Sync
import cats.syntax.all._
import io.circe.Encoder
import me.jooohn.stayed.adapter.service.protocol.userlocation.UserLocationsCommand.Create
import me.jooohn.stayed.adapter.service.protocol.userlocation._
import me.jooohn.stayed.domain.{UserAccount, UserLocation}
import me.jooohn.stayed.usecase.UserLocationUseCase
import me.jooohn.stayed.usecase.UserLocationUseCase.Error
import org.http4s.{AuthedService, Response}

class UserLocationService[F[_]: Monad: Sync](useCase: UserLocationUseCase[F])
    extends BaseService[F] {
  import UserLocationCommand._

  val service: AuthedService[UserAccount, F] = AuthedService[UserAccount, F] {

    case GET -> Root / "locations" as userAccount =>
      for {
        userLocations <- useCase.list(userAccount.userId)
        response <- Ok(userLocations)
      } yield response

    case req @ POST -> Root / "locations" as userAccount =>
      req.req.as[UserLocationsCommand] flatMap {
        case Create(label) =>
          useCase.create(userAccount.userId, label) flatMap (Created(_))
      }

    case req @ POST -> Root / "locations" / userLocationIdStr as userAccount =>
      val userLocationId = UserLocation.Id(userLocationIdStr)
      req.req.as[UserLocationCommand] flatMap {
        case Enter(at) =>
          useCase.enter(userAccount.userId, userLocationId, at) respond (_ => Ok())
        case Exit(at) =>
          useCase.exit(userAccount.userId, userLocationId, at) respond (_ => Ok())
      }

  }

  implicit class EitherTErrorOps[A](fa: EitherT[F, Error, A]) {

    def respond(f: A => F[Response[F]]): F[Response[F]] =
      fa.fold[F[Response[F]]](error => BadRequest(error.message), f).flatten

  }
}
