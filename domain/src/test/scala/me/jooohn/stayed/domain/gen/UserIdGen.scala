package me.jooohn.stayed.domain.gen

import me.jooohn.stayed.domain.UserId
import org.scalacheck.Gen

trait UserIdGen {

  val userIdGen: Gen[UserId] = Gen.alphaNumStr.map(UserId.apply)

}
object UserIdGen extends UserIdGen
