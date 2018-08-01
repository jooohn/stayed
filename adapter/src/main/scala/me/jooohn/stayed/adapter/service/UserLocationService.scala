package me.jooohn.stayed.adapter.service

import java.time.{YearMonth, ZoneId}

import cats.Monad
import cats.data.EitherT
import cats.effect.Sync
import cats.syntax.all._
import me.jooohn.stayed.adapter.service.protocol.userlocation._
import me.jooohn.stayed.domain.{UserAccount, UserLocation}
import me.jooohn.stayed.usecase.UserLocationUseCase
import me.jooohn.stayed.usecase.UserLocationUseCase.Error
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.{AuthedService, QueryParamDecoder, Response}

class UserLocationService[F[_]: Monad: Sync](userLocationUseCase: UserLocationUseCase[F])
    extends BaseService[F] {
  import UserLocationCommand._
  import UserLocationService._

  val service: AuthedService[UserAccount, F] = AuthedService[UserAccount, F] {

    case GET -> Root / "locations" / userLocationId / "transactions" :? YearMonthQuery(yearMonth) +& TimeZoneQuery(
          timeZone) as userAccount =>
      userLocationUseCase.resolve(userAccount.userId, UserLocation.Id(userLocationId)) respond {
        userLocation =>
          Ok(userLocation.staysWithin(yearMonth, timeZone))
      }

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

object UserLocationService {
  implicit val yearMonthQueryParamDecoder: QueryParamDecoder[YearMonth] =
    QueryParamDecoder[String].map(YearMonth.parse)

  object YearMonthQuery extends QueryParamDecoderMatcher[YearMonth]("ym")

  implicit val timeZoneQueryParamDecoder: QueryParamDecoder[ZoneId] =
    QueryParamDecoder[String].map(ZoneId.of)

  object TimeZoneQuery extends QueryParamDecoderMatcher[ZoneId]("timezone")

}
