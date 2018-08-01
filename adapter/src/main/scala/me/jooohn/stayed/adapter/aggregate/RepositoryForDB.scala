package me.jooohn.stayed.adapter.aggregate

import cats.Monad
import doobie._
import doobie.implicits._
import me.jooohn.stayed.adapter.DoobieFeatures

trait RepositoryForDB[F[_]] extends DoobieFeatures {

  def transactor: Transactor[F]

  def transaction[A](connectionIO: ConnectionIO[A])(implicit M: Monad[F]): F[A] =
    connectionIO.transact(transactor)

}
