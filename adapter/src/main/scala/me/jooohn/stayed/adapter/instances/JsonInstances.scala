package me.jooohn.stayed.adapter.instances
import doobie.util.meta.Meta
import cats.implicits._
import io.circe.Json
import io.circe.jawn._
import org.postgresql.util.PGobject

trait JsonInstances {

  implicit val JsonMeta: Meta[Json] =
    Meta
      .other[PGobject]("json")
      .xmap[Json](
        a => parse(a.getValue).leftMap[Json](e => throw e).merge,
        a => {
          val o = new PGobject
          o.setType("json")
          o.setValue(a.noSpaces)
          o
        }
      )

}
