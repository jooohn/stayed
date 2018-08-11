package me.jooohn.stayed.adapter.aggregate

import cats.Monad
import doobie._
import doobie.implicits._

trait RepositoryForDB[F[_]] {

  def transactor: Transactor[F]

  def transaction[A](connectionIO: ConnectionIO[A])(implicit M: Monad[F]): F[A] =
    connectionIO.transact(transactor)

}
