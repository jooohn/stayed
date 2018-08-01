package me.jooohn.stayed

import cats.Monad
import cats.data.{Kleisli, OptionT}
import me.jooohn.stayed.adapter.FirebaseAuthenticator
import me.jooohn.stayed.domain.UserAccount
import org.http4s.Request
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

class Authentication[F[_]: Monad](authenticator: FirebaseAuthenticator) {

  def middleware: AuthMiddleware[F, UserAccount] = AuthMiddleware(authUser)

  def authUser: Kleisli[OptionT[F, ?], Request[F], UserAccount] =
    Kleisli { request =>
      for {
        authorization <- request.authorization
        userAccount <- authenticate(authorization.value)
      } yield userAccount
    }

  private def authenticate(idToken: String): OptionT[F, UserAccount] =
    OptionT.fromOption[F](authenticator.authenticate(idToken).right.toOption)

  implicit class RichRequest(request: Request[F]) {

    def authorization: OptionT[F, Authorization] =
      OptionT.fromOption[F](request.headers.get(Authorization))

  }

}
