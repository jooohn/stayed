package me.jooohn.stayed.domain
import me.jooohn.stayed.infrastructure.Iso

case class UserId(value: String) extends AnyVal {
  override def toString: String = value
}

object UserId extends UserIdInstances

private[domain] trait UserIdInstances {

  implicit val userIdIso: Iso[String, UserId] =
    Iso.newInstance(_.value, UserId.apply)

}
