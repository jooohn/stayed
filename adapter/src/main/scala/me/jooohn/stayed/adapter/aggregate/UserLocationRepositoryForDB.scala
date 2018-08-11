package me.jooohn.stayed.adapter.aggregate

import java.util.UUID

import cats.Monad
import doobie._
import doobie.implicits._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import me.jooohn.stayed.adapter.instances.{AllInstances, UserLocationInstances}
import me.jooohn.stayed.domain.UserLocation.{State, Stay}
import me.jooohn.stayed.domain.{UserId, UserLocation}
import me.jooohn.stayed.usecase.repository.UserLocationRepository

class UserLocationRepositoryForDB[F[_]: Monad](val transactor: Transactor[F])
    extends UserLocationRepository[F]
    with RepositoryForDB[F] {
  import UserLocationRepositoryForDB._

  override def findAllBy(userId: UserId): F[List[UserLocation]] =
    transaction {
      sql"SELECT * FROM user_locations WHERE user_id = $userId".query[UserLocation].to[List]
    }

  override def resolveBy(userId: UserId, id: UserLocation.Id): F[Option[UserLocation]] =
    transaction {
      sql"SELECT * FROM user_locations WHERE id = $id AND user_id = $userId"
        .query[UserLocation]
        .option
    }

  override def create(userId: UserId, label: String): F[UserLocation] =
    transaction {
      val userLocation = newUserLocation(userId, label)
      sql"""INSERT INTO user_locations VALUES (${userLocation.id}, ${userLocation.userId}, ${userLocation.data})""".update.run
        .map(_ => userLocation)
    }

  override def update(userLocation: UserLocation): F[Unit] = transaction {
    sql"UPDATE user_locations SET data = ${userLocation.data} WHERE id = ${userLocation.id}".update.run
      .map(_ => ())
  }

  private def newUserLocation(userId: UserId, label: String): UserLocation = {
    val id: UserLocation.Id = UserLocation.Id(UUID.randomUUID().toString)
    UserLocation.create(id, userId, label)
  }
}

object UserLocationRepositoryForDB extends AllInstances {

  case class Data(label: String, state: State, transactions: List[Stay])

  implicit val dataEncoder: Encoder[Data] = deriveEncoder[Data]
  implicit val dataDecoder: Decoder[Data] = deriveDecoder[Data]
  implicit val dataMeta: Meta[Data] = codedMeta[Data]

  implicit val userLocationComposite: Composite[UserLocation] =
    Composite[(UserLocation.Id, UserId, Data)].imap {
      case (id, userId, Data(label, state, transactions)) =>
        UserLocation(id, userId, label, state, transactions)
    }(userLocation => (userLocation.id, userLocation.userId, userLocation.data))

  implicit class RichUserLocation(userLocation: UserLocation) {

    def data: Data = Data(userLocation.label, userLocation.state, userLocation.stays)

  }
}
