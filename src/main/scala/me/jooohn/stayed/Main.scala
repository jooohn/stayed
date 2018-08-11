package me.jooohn.stayed
import cats.implicits._
import cats.effect.IO
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import doobie.util.transactor.Transactor
import fs2.StreamApp
import me.jooohn.stayed.adapter.aggregate.{UserLocationRepositoryForDB, UserSettingRepositoryForDB}
import me.jooohn.stayed.adapter.service.{UserLocationService, UserSettingService}
import me.jooohn.stayed.adapter.{FirebaseAuthenticator, UUIDApiTokenGenerator}
import me.jooohn.stayed.usecase.{UserLocationUseCase, UserSettingUseCase}
import org.http4s.server.blaze._
import org.http4s._

import scala.concurrent.ExecutionContext

object Main extends StreamApp[IO] {
  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private val config: StayedConfig = StayedConfig.load.fold(
    errors =>
      throw new RuntimeException(
        s"Config failure: ${errors.map(_.message).toList.mkString("\n")}"
    ),
    identity
  )

  private val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    config.postgresql.url,
    config.postgresql.user,
    config.postgresql.pass
  )

  private val firebaseAuthenticator =
    new FirebaseAuthenticator(
      FirebaseAuth.getInstance(FirebaseApp.initializeApp(config.firebaseOptions)))
  private val withAuthentication = new Authentication[IO](firebaseAuthenticator).middleware

  private val userLocationRepository = new UserLocationRepositoryForDB(transactor)
  private val userSettingRepository = new UserSettingRepositoryForDB(transactor)
  private val apiTokenGenerator = new UUIDApiTokenGenerator[IO]

  private val services: HttpService[IO] =
    List(
      new UserLocationService(
        new UserLocationUseCase(userLocationRepository)
      ).service,
      new UserSettingService(
        new UserSettingUseCase(
          userSettingRepository,
          apiTokenGenerator
        )
      ).service
    ).map(withAuthentication).reduce(_ <+> _)

  override def stream(
      args: List[String],
      requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(config.server.port, "localhost")
      .mountService(services, "/api")
      .serve

}
