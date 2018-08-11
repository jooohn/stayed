package me.jooohn.stayed.domain

import java.time._

import shapeless.tag
import shapeless.tag.@@

case class UserLocation(
    id: UserLocation.Id,
    userId: UserId,
    label: String,
    state: UserLocation.State,
    stays: List[UserLocation.Stay]
) {
  import UserLocation._
  import State._

  type ErrorOr[A] = Either[Error, A]

  def entered(at: Instant): ErrorOr[UserLocation] =
    (state, stays.headOption) match {
      case (Exited, Some(lastTransaction)) if !lastTransaction.isBefore(at) =>
        Left(EnterBeforeExited(id, at))
      case (Exited, _) =>
        Right(copy(state = Entered(at)))
      case (Entered(enteredAt), _) =>
        Left(AlreadyEntered(id, enteredAt))
    }

  def exited(at: Instant): ErrorOr[UserLocation] = state match {

    case Entered(enteredAt) =>
      if (enteredAt.isBefore(at)) {
        val newTransaction = Stay(from = enteredAt, to = at)
        val exited = copy(
          state = Exited,
          stays = newTransaction :: stays
        )
        Right(exited.discardOldStays)
      } else Left(ExitBeforeEntered(id, at))

    case Exited =>
      Left(AlreadyExited(id))

  }

  def discardOldStays: UserLocation = stays match {
    case Nil             => this
    case latestStay :: _ =>
      // Keep more than 3 months
      val borderInstant =
        latestStay.from.minusSeconds(60 * 24 * keepStayDays)
      copy(stays = stays.takeWhile(!_.isBefore(borderInstant)))
  }

  def staysWithin(yearMonth: YearMonth, zoneId: ZoneId): List[Stay] =
    stays.filter(_.isWithin(yearMonth, zoneId))

}

object UserLocation {

  val keepStayDays: Int = 100

  type Id = String @@ UserLocation
  object Id {
    def apply(value: String): Id = tag[UserLocation][String](value)
  }

  sealed abstract class State
  object State {
    case class Entered(at: Instant) extends State
    case object Exited extends State
  }

  case class Stay(from: Instant, to: Instant) {
    require(from.isBefore(to))

    def fromDate(atZone: ZoneId): LocalDate =
      from.atZone(atZone).toLocalDate

    def toDate(atZone: ZoneId): LocalDate =
      to.atZone(atZone).toLocalDate

    def isBefore(instant: Instant): Boolean = to.isBefore(instant)

    def isWithin(yearMonth: YearMonth, zoneId: ZoneId): Boolean = {
      val fromYearMonth = fromDate(zoneId).toYearMonth
      val toYearMonth = toDate(zoneId).toYearMonth
      !yearMonth.isBefore(fromYearMonth) && !yearMonth.isAfter(toYearMonth)
    }

  }

  sealed abstract class Error(override val message: String) extends DomainError

  case class AlreadyEntered(useLocationId: UserLocation.Id, at: Instant)
      extends Error(s"$useLocationId has already entered at $at.")

  case class AlreadyExited(userLocationId: UserLocation.Id)
      extends Error(s"$userLocationId has already exited.")

  case class ExitBeforeEntered(userLocationId: UserLocation.Id, at: Instant)
      extends Error(s"$userLocationId tried to exit, but it has entered before $at")

  case class EnterBeforeExited(userLocationId: UserLocation.Id, at: Instant)
      extends Error(s"$userLocationId tried to enter, but it has exited before $at.")

  implicit class LocalDateOps(localDate: LocalDate) {

    def toYearMonth: YearMonth = YearMonth.from(localDate)

  }

  def create(id: Id, userId: UserId, label: String): UserLocation =
    UserLocation(id, userId, label, State.Exited, stays = Nil)
}
