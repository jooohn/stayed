package me.jooohn.stayed.usecase.repository
import me.jooohn.stayed.domain.ApiToken

trait ApiTokenGenerator[F[_]] {

  def generate: F[ApiToken]

}
