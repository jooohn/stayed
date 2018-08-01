package me.jooohn.stayed.adapter.aggregate

import java.time.Instant
import java.util.UUID

import cats.Monad
import doobie._
import doobie.implicits._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import me.jooohn.stayed.adapter.{CirceFeatures, DoobieFeatures}
import me.jooohn.stayed.domain.UserLocation.{State, Stay}
import me.jooohn.stayed.domain.{UserId, UserLocation}
import me.jooohn.stayed.usecase.repository.UserLocationRepository

class UserLocationRepositoryForDB[F[_]: Monad](val transactor: Transactor[F])
    extends UserLocationRepository[F]
    with RepositoryForDB[F] {
  import UserLocationRepositoryForDB._

  override def create(userId: UserId, label: String): F[UserLocation] = transaction {
    val userLocation = newUserLocation(userId, label)
    sql"""INSERT INTO user_locations VALUES (${userLocation.id}, ${userLocation.userId}, ${userLocation.data})""".update.run
      .map(_ => userLocation)
  }

  override def resolveBy(userId: UserId, id: UserLocation.Id): F[Option[UserLocation]] =
    transaction {
      sql"SELECT * FROM user_locations WHERE id = $id AND user_id = $userId"
        .query[UserLocation]
        .option
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

object UserLocationRepositoryForDB extends DoobieFeatures with CirceFeatures {

  case class Data(label: String, state: State, transactions: List[Stay])

  implicit val stateEncoder: Encoder[State] =
    Encoder[Option[Instant]].contramap {
      case State.Entered(at) => Some(at)
      case State.Exited      => None
    }
  implicit val transactionEncoder: Encoder[Stay] =
    deriveEncoder[Stay]
  implicit val dataEncoder: Encoder[Data] = deriveEncoder[Data]

  implicit val stateDecoder: Decoder[State] = Decoder[Option[Instant]].map {
    case Some(at) => State.Entered(at)
    case None     => State.Exited
  }
  implicit val transactionDecoder: Decoder[Stay] =
    deriveDecoder[Stay]
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
