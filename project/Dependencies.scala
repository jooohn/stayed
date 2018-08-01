import sbt._

object Dependencies {
  object Config {
    lazy val configVersion = "1.3.2"
    lazy val config = "com.typesafe" % "config" % configVersion
  }

  object Monocle {
    lazy val monocleVersion = "1.5.0"
    lazy val core = "com.github.julien-truffaut" %% "monocle-core" % monocleVersion
    lazy val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion
    lazy val all = Seq(core, monocleMacro)
  }

  object Cats {
    lazy val catsVersion = "1.1.0"
    lazy val core = "org.typelevel" %% "cats-core" % catsVersion
  }

  object Shapeless {
    lazy val shapelessVersion = "2.3.3"
    lazy val core = "com.chuusai" %% "shapeless" % shapelessVersion
    lazy val all = Seq(core)
  }

  object Http4s {

    lazy val http4sVersion = "0.18.5"
    lazy val dsl = "org.http4s" %% "http4s-dsl" % http4sVersion
    lazy val blazeServer = "org.http4s" %% "http4s-blaze-server" % http4sVersion
    lazy val circe = "org.http4s" %% "http4s-circe" % http4sVersion

    lazy val all = Seq(dsl, blazeServer, circe)
  }

  object Firebase {
    val firebaseVersion = "6.3.0"
    val admin = "com.google.firebase" % "firebase-admin" % firebaseVersion
    lazy val all = Seq(admin)
  }

  object Doobie {
    lazy val doobieVersion = "0.5.3"
    lazy val core = "org.tpolecat" %% "doobie-core" % doobieVersion
    lazy val postgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion // Postgres driver 42.2.2 + type mappings.
    lazy val scalatest = "org.tpolecat" %% "doobie-scalatest" % doobieVersion // ScalaTest support for typechecking statements.
    lazy val all = Seq(
      core,
      postgres,
      scalatest
    )
    //    "org.tpolecat" %% "doobie-h2"        % "0.5.3", // H2 driver 1.4.197 + type mappings.
    //    "org.tpolecat" %% "doobie-hikari"    % "0.5.3", // HikariCP transactor.
    //    "org.tpolecat" %% "doobie-specs2"    % "0.5.3", // Specs2 support for typechecking statements.
  }

  object Circe {
    lazy val circeVersion = "0.9.3"

    val core = "io.circe" %% "circe-core" % circeVersion
    val generic = "io.circe" %% "circe-generic" % circeVersion
    val genericExtras = "io.circe" %% "circe-generic-extras" % circeVersion
    val shapes = "io.circe" %% "circe-shapes" % circeVersion
    val parser = "io.circe" %% "circe-parser" % circeVersion
    val all = Seq(core, generic, genericExtras, shapes, parser)
  }

  object TestDependencies {
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    val all = Seq(scalaTest, scalaCheck)
  }

  object Logging {
    val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.25"

    val all = Seq(slf4jSimple)
  }
}
