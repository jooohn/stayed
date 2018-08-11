package me.jooohn.stayed.adapter
import java.util.UUID

import cats.Applicative
import me.jooohn.stayed.domain.ApiToken
import me.jooohn.stayed.usecase.repository.ApiTokenGenerator

class UUIDApiTokenGenerator[F[_]: Applicative] extends ApiTokenGenerator[F] {
  override def generate: F[ApiToken] = Applicative[F].pure(ApiToken(UUID.randomUUID().toString))
}
