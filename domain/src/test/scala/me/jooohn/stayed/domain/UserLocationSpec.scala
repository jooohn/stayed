package me.jooohn.stayed.domain

import java.time.{Instant, YearMonth}

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

class UserLocationSpec extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {
  import gen.UserLocationGen._

  describe("#enter") {

    it("enters if exited without transactions") {
      val gen = for {
        userLocation <- userLocationGen.map(
          _.copy(
            state = UserLocation.State.Exited,
            stays = Nil
          ))
        enterAt <- instantGen
      } yield (userLocation, enterAt)
      forAll(gen) {
        case (userLocation, enterAt) =>
          userLocation.entered(enterAt).isRight should be(true)
      }
    }

    it("enters if exited with past transactions") {
      val gen = for {
        transactions <- nonEmptyStaysGen
        userLocation <- userLocationGen.map(
          _.copy(
            state = UserLocation.State.Exited,
            stays = transactions
          ))
        enterAt <- laterInstantGen(transactions.head.to)
      } yield (userLocation, enterAt)
      forAll(gen) {
        case (userLocation, enterAt) =>
          userLocation.entered(enterAt).isRight should be(true)
      }
    }

    it("fails if exited with future transaction") {
      val gen = for {
        transactions <- nonEmptyStaysGen
        userLocation <- userLocationGen.map(
          _.copy(
            state = UserLocation.State.Exited,
            stays = transactions
          ))
        enterAt <- earlierInstantGen(transactions.head.to)
      } yield (userLocation, enterAt)

      forAll(gen) {
        case (userLocation, enterAt) =>
          userLocation.entered(enterAt).isLeft should be(true)
      }
    }

  }

  describe("#exit") {

    it("exits if entered in the past") {
      val gen = for {
        transactions <- staysGen
        enteredAt <- transactions.headOption.fold(instantGen)(t => laterInstantGen(t.to))
        zoneId <- zoneIdGen
        userLocation <- userLocationGen.map(
          _.copy(
            state = UserLocation.State.Entered(enteredAt),
            stays = Nil
          ))
        exitAt <- laterInstantGen(enteredAt)
      } yield (userLocation, exitAt, zoneId)
      forAll(gen) {
        case (userLocation, exitAt, zoneId) =>
          val result = userLocation.exited(exitAt, zoneId)
          result.isRight should be(true)

          val border = Instant.from(
            YearMonth
              .from(exitAt.atZone(zoneId))
              .minusMonths(3)
              .atDay(1)
              .atStartOfDay(zoneId))
          result.right.get.stays.exists(stay => stay.isBefore(border)) should be(false)
      }
    }

    it("fails if entered in the future") {
      val gen = for {
        transactions <- staysGen
        enteredAt <- transactions.headOption.fold(instantGen)(t => laterInstantGen(t.to))
        userLocation <- userLocationGen.map(
          _.copy(
            state = UserLocation.State.Entered(enteredAt),
            stays = Nil
          ))
        exitAt <- earlierInstantGen(enteredAt)
        timeZone <- zoneIdGen
      } yield (userLocation, exitAt, timeZone)
      forAll(gen) {
        case (userLocation, exitAt, timeZone) =>
          userLocation.exited(exitAt, timeZone).isLeft should be(true)
      }
    }

    it("fails if exited") {
      val gen = for {
        userLocation <- userLocationGen.map(
          _.copy(
            state = UserLocation.State.Exited
          ))
        exitAt <- instantGen
        timeZone <- zoneIdGen
      } yield (userLocation, exitAt, timeZone)

      forAll(gen) {
        case (userLocation, exitAt, timeZone) =>
          userLocation.exited(exitAt, timeZone).isLeft should be(true)
      }
    }

  }

  private def laterInstantGen(instant: Instant): Gen[Instant] =
    Gen.choose(1, 60 * 24 * 365).map { elapsedSeconds =>
      instant.plusSeconds(elapsedSeconds)
    }

  private def earlierInstantGen(instant: Instant): Gen[Instant] =
    Gen.choose(1, 60 * 24 * 365).map { elapsedSeconds =>
      instant.minusSeconds(elapsedSeconds)
    }

  describe("Transaction") {
    describe("isWithin") {
      val g = for {
        transaction <- staysGenN(1).map(_.head)
        zoneId <- zoneIdGen
      } yield (transaction, zoneId)

      it("is true on from/to year month") {
        forAll(g) {
          case (transaction, zoneId) =>
            val fromYearMonth = YearMonth.from(transaction.fromDate(zoneId))
            transaction.isWithin(fromYearMonth, zoneId) should be(true)

            val toYearMonth = YearMonth.from(transaction.toDate(zoneId))
            transaction.isWithin(toYearMonth, zoneId) should be(true)
        }
      }

      it("is false on out of from/to range") {
        forAll(g) {
          case (transaction, timeZone) =>
            val fromYearMonth = YearMonth.from(transaction.fromDate(timeZone))
            transaction.isWithin(fromYearMonth.minusMonths(1), timeZone) should be(false)

            val toYearMonth = YearMonth.from(transaction.toDate(timeZone))
            transaction.isWithin(toYearMonth.plusMonths(1), timeZone) should be(false)
        }
      }

    }

  }

}
