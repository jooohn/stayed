package me.jooohn.stayed.usercase

import java.time.temporal.{ChronoUnit, TemporalUnit}

import cats.data.EitherT
import cats.instances.try_._
import me.jooohn.stayed.usecase.UserLocationUseCase
import me.jooohn.stayed.usecase.repository.UserLocationRepository
import me.jooohn.stayed.usercase.repository.InMemoryUserLocationRepository
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.util.Try

class UserLocationUseCaseSpec extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {
  import me.jooohn.stayed.domain.gen.UserIdGen._
  import me.jooohn.stayed.domain.gen.UserLocationGen._

  describe("general scenario") {

    val paramsGen = for {
      userId <- userIdGen
      label <- Gen.alphaNumStr
      enterAt <- instantGen
      stayMinutes <- Gen.chooseNum(1, 300)
    } yield (userId, label, enterAt, enterAt.plusSeconds(60 * stayMinutes))

    it("creates, enter and then exit") {
      forAll(paramsGen) {
        case (userId, label, enterAt, exitAt) =>
          val userLocationRepository = new InMemoryUserLocationRepository()
          val useCase = newUseCase(userLocationRepository = userLocationRepository)

          val id = (for {
            created <- EitherT.right(useCase.create(userId, label))
            _ <- useCase.enter(userId, created.id, enterAt)
            _ <- useCase.exit(userId, created.id, exitAt)
          } yield created.id).value.get.right.get

          val stored = userLocationRepository.map(id)
          stored.stays.length should be(1)
      }
    }

  }

  describe("exit with too old stays") {

    it("discards old stays") {
      forAll(for {
        userLocation <- exitedUserLocationGen
        stays <- nonEmptyStaysGen
        daysAfter <- Gen.chooseNum(101, 300)
      } yield (userLocation.copy(stays = stays), daysAfter)) {
        case (userLocation, d) =>
          val userLocationRepository =
            new InMemoryUserLocationRepository(
              collection.mutable.Map(
                userLocation.id -> userLocation
              ))

          val nextEnterAt =
            userLocation.stays.head.to.plus(d, ChronoUnit.DAYS)
          val nextExitAt = nextEnterAt.plusSeconds(100)

          val useCase = newUseCase(userLocationRepository = userLocationRepository)

          val result = for {
            _ <- useCase.enter(userLocation.userId, userLocation.id, nextEnterAt)
            _ <- useCase.exit(userLocation.userId, userLocation.id, nextExitAt)
          } yield ()
          result.isRight.get should equal(true)

          val stored = userLocationRepository.map(userLocation.id)
          stored.stays.length should be(1)
      }
    }
  }

  def newUseCase(
      userLocationRepository: UserLocationRepository[Try] = new InMemoryUserLocationRepository()
  ): UserLocationUseCase[Try] = new UserLocationUseCase[Try](
    userLocationRepository
  )

}
