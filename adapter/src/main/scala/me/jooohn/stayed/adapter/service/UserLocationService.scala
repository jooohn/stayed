package me.jooohn.stayed.adapter.service

import cats.Monad
import cats.data.EitherT
import cats.effect.Sync
import cats.syntax.all._
import io.circe.Encoder
import me.jooohn.stayed.adapter.service.protocol.userlocation._
import me.jooohn.stayed.domain.{UserAccount, UserLocation}
import me.jooohn.stayed.usecase.UserLocationUseCase
import me.jooohn.stayed.usecase.UserLocationUseCase.Error
import org.http4s.{AuthedService, Response}

class UserLocationService[F[_]: Monad: Sync](userLocationUseCase: UserLocationUseCase[F])
    extends BaseService[F] {
  import UserLocationCommand._

  val service: AuthedService[UserAccount, F] = AuthedService[UserAccount, F] {

    case GET -> Root / "locations" as userAccount =>
      for {
        userLocations <- userLocationUseCase.list(userAccount.userId)
        response <- Ok(userLocations)
      } yield response

    case req @ POST -> Root / "locations" as userAccount =>
      for {
        command <- req.req.as[UserLocationCommand]
        response <- handleCommand(userAccount, command)
      } yield response

  }

  def handleCommand(userAccount: UserAccount, command: UserLocationCommand): F[Response[F]] =
    command match {
      case Create(label) =>
        userLocationUseCase.create(userAccount.userId, label) flatMap { userLocation =>
          Created(UserLocationResponse.Created(userLocation.id))
        }
      case Enter(userLocationId, at) =>
        userLocationUseCase.enter(userAccount.userId, userLocationId, at) respond { _ =>
          Ok()
        }
      case Exit(userLocationId, at) =>
        userLocationUseCase.exit(userAccount.userId, userLocationId, at) respond { _ =>
          Ok()
        }
    }

  implicit class EitherTErrorOps[A](fa: EitherT[F, Error, A]) {

    def respond(f: A => F[Response[F]]): F[Response[F]] =
      fa.fold[F[Response[F]]](error => BadRequest(error.message), f).flatten

  }
}
