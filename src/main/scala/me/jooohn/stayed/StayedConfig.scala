package me.jooohn.stayed

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}

import cats.data.ValidatedNel
import cats.implicits._
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase.auth.FirebaseAuth
import com.typesafe.config.{Config, ConfigFactory}

import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

case class StayedConfig(
    server: ServerConfig,
    postgresql: PostgresqlConfig,
    firebaseOptions: FirebaseOptions
)

case class ServerConfig(
    port: Int
)

case class PostgresqlConfig(
    url: String,
    user: String,
    pass: String
)

object StayedConfig {
  case class LoadError(message: String)

  type ErrorOr[A] = ValidatedNel[LoadError, A]

  def load: ErrorOr[StayedConfig] = {
    Try(ConfigFactory.load()).fold(
      e => LoadError(e.getMessage).invalidNel,
      config =>
        (
          loadServerConfig(config),
          loadPostgresqlConfig(config),
          loadFirebaseOptions(config)
        ).mapN(StayedConfig.apply)
    )
  }

  private def loadServerConfig(config: Config): ErrorOr[ServerConfig] =
    config.safeInt("server.port") map { port =>
      ServerConfig(port)
    }

  private def loadPostgresqlConfig(config: Config): ErrorOr[PostgresqlConfig] =
    (
      config.safeString("postgresql.url"),
      config.safeString("postgresql.user"),
      config.safeString("postgresql.pass")
    ).mapN(PostgresqlConfig.apply)

  private def loadFirebaseOptions(config: Config): ErrorOr[FirebaseOptions] =
    config.safeString("firebase.serviceAccountKey").andThen { key =>
      Try {
        new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(key.getBytes)))
          .build()
      }.toEither.leftMap(e => LoadError(e.getMessage)).toValidatedNel
    }

  implicit class RichConfig(config: Config) {

    val safeConfig: String => ErrorOr[Config] = safe(config.getConfig)
    val safeInt: String => ErrorOr[Int] = safe(config.getInt)
    val safeString: String => ErrorOr[String] = safe(config.getString)

    private def safe[A](get: String => A)(implicit T: TypeTag[A]): String => ErrorOr[A] =
      (path: String) => {
        if (config.hasPath(path))
          Try(get(path)).fold(
            _ => LoadError(s"Failed to get ${path} as ${T.toString()}").invalidNel,
            _.valid
          )
        else LoadError(s"Required config ${path} is not defined").invalidNel
      }

  }

}
