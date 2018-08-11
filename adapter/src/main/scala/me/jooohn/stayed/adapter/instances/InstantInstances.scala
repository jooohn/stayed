package me.jooohn.stayed.adapter.instances
import java.time.Instant

import me.jooohn.stayed.infrastructure.Iso

trait InstantInstances {

  implicit val instantIso: Iso[Long, Instant] =
    Iso.newInstance(
      _.toEpochMilli,
      Instant.ofEpochMilli
    )

}
