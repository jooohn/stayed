package me.jooohn.stayed.domain

import java.time.{ZoneId, ZoneOffset}

case class UserSetting(userId: UserId, zoneId: ZoneId)
object UserSetting {

  def default(userId: UserId): UserSetting =
    UserSetting(userId, zoneId = ZoneId.ofOffset("UTC", ZoneOffset.UTC))

}
