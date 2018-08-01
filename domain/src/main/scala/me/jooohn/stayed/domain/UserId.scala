package me.jooohn.stayed.domain

case class UserId(value: String) extends AnyVal {
  override def toString: String = value
}
