package me.jooohn.stayed.domain
import me.jooohn.stayed.infrastructure.Iso

case class ApiToken(value: String) extends AnyVal {
  override def toString: String = value
}

object ApiToken extends ApiTokenInstances

private[domain] trait ApiTokenInstances {

  implicit val apiTokenIso: Iso[String, ApiToken] =
    Iso.newInstance(_.toString, ApiToken.apply)

}
