package me.jooohn.stayed
import cats.Monad
import cats.effect.Sync
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpService, Request, StaticFile}

class StaticFileService[F[_]: Monad: Sync] extends Http4sDsl[F] {

  def static(file: String, request: Request[F]) =
    StaticFile.fromResource("/static/" + file, Some(request)).getOrElseF(NotFound())

  val service = HttpService[F] {

    case request @ GET -> Root / path
        if List(".js", ".css", ".map", ".html", ".json", ".ico", ".jpg").exists(path.endsWith) =>
      static(path, request)

    case request @ GET -> base /: rest
        if List(".js", ".css", ".map", ".html", ".json", ".ico", ".jpg").exists(
          rest.toList.mkString("/").endsWith) =>
      static((base :: rest.toList).mkString("/"), request)

    case request @ GET -> Root =>
      static("index.html", request)

    case request @ GET -> _ =>
      static("index.html", request)

    case request @ GET -> _ /: _ =>
      static("index.html", request)
  }

}
object StaticFileService {

  def apply[F[_]: Monad: Sync]: HttpService[F] = new StaticFileService[F]().service

}
