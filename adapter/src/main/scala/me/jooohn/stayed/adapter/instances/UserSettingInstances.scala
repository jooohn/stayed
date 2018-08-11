package me.jooohn.stayed.adapter.instances
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import me.jooohn.stayed.domain.UserSetting

trait UserSettingInstances {
  import lens._

  implicit val userSettingEncoder: Encoder[UserSetting] =
    deriveEncoder[UserSetting]

  implicit val userSettingDecoder: Decoder[UserSetting] =
    deriveDecoder[UserSetting]
}
