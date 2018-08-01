package me.jooohn.stayed.usercase.repository

import scala.collection.mutable
import scala.util.Try

class InMemoryStore[Id, Record](map: mutable.Map[Id, Record] = mutable.Map.empty)(
    idOf: Record => Id) {

  def insert(record: Record): Try[Unit] = Try {
    val id = idOf(record)
    require(map.get(id).isEmpty)

    map.update(id, record)
  }

  def update(record: Record): Try[Unit] = Try {
    val id = idOf(record)
    require(map.get(id).isDefined)

    map.update(id, record)
  }

  def find(id: Id): Try[Option[Record]] = Try(map.get(id))

}
