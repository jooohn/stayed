package me.jooohn.stayed.domain.gen

import java.time.{Instant, ZoneId, ZoneOffset}
import java.util.TimeZone

import me.jooohn.stayed.domain.UserLocation.State.Exited
import me.jooohn.stayed.domain._
import org.scalacheck.Gen
import shapeless.tag

object UserLocationGen {

  val instantMin: Instant = Instant.now().minusSeconds(60 * 24 * 365 * 100)
  val instantMax: Instant = Instant.now().plusSeconds(60 * 24 * 365 * 100)
  val instantGen: Gen[Instant] = Gen
    .choose(instantMin.toEpochMilli, instantMax.toEpochMilli)
    .map(Instant.ofEpochMilli)

  val zoneIdGen: Gen[ZoneId] = Gen.choose(-12, 12).map { offset =>
    ZoneId.ofOffset("UTC", ZoneOffset.ofHours(offset))
  }

  val idGen: Gen[UserLocation.Id] = Gen.alphaNumStr.map(tag[UserLocation][String])

  val enteredStateGen: Gen[UserLocation.State.Entered] =
    instantGen.map(UserLocation.State.Entered)
  val stateGen: Gen[UserLocation.State] = Gen.oneOf(
    Gen.const(UserLocation.State.Exited: UserLocation.State),
    enteredStateGen
  )

  def staysGenN(n: Int): Gen[List[UserLocation.Stay]] =
    if (n == 0) Gen.const(Nil)
    else
      for {
        samples <- Gen.listOfN(2, instantGen)
        list <- {
          val min = samples.min
          val max = samples.max
          val interval = (max.toEpochMilli - min.toEpochMilli) / n
          Gen.sequence[List[UserLocation.Stay], UserLocation.Stay] {
            (1 to n) map { i =>
              val thisInstantGen =
                Gen.listOfN(2, Gen.choose(min.toEpochMilli, min.toEpochMilli + interval * i))
              thisInstantGen.map { millis =>
                UserLocation.Stay(
                  from = Instant.ofEpochMilli(millis.min),
                  to = Instant.ofEpochMilli(millis.max)
                )
              }
            }
          }
        }
      } yield list.reverse

  val staysGen: Gen[List[UserLocation.Stay]] = Gen.choose(0, 30).flatMap(staysGenN)

  val nonEmptyStaysGen: Gen[List[UserLocation.Stay]] =
    for {
      size <- Gen.choose(1, 30)
      stays <- staysGenN(size)
    } yield stays

  val userLocationGen: Gen[UserLocation] =
    for {
      id <- idGen
      userId <- UserIdGen.userIdGen
      label <- Gen.alphaNumStr
      state <- stateGen
      stays <- staysGen
    } yield UserLocation(id, userId, label, state, stays)

  val exitedUserLocationGen: Gen[UserLocation] =
    userLocationGen.map { userLocation =>
      userLocation.copy(state = Exited)
    }

}
