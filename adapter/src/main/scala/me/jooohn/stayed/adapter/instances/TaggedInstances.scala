package me.jooohn.stayed.adapter.instances
import me.jooohn.stayed.infrastructure.Iso
import shapeless.tag
import shapeless.tag.@@

trait TaggedInstances {

  implicit def taggedIso[A, B]: Iso[A, A @@ B] = Iso.newInstance(
    identity,
    tag[B][A]
  )

}
