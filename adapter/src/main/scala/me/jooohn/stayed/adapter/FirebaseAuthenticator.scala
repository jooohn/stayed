package me.jooohn.stayed.adapter

import cats.implicits._
import com.google.firebase.auth.FirebaseAuth
import me.jooohn.stayed.domain.{UserAccount, UserId}

import scala.util.Try

class FirebaseAuthenticator(auth: FirebaseAuth) {
  import FirebaseAuthenticator._

  def authenticate(idToken: String): AuthenticationResult =
    Try(auth.verifyIdToken(idToken)).fold(
      e => AuthenticationError(e.getMessage).asLeft,
      token => UserAccount(UserId(token.getUid), token.getName).asRight
    )

}

object FirebaseAuthenticator {

  type AuthenticationResult = Either[AuthenticationError, UserAccount]

  case class AuthenticationError(message: String)

}
