import Dependencies._

lazy val baseSettings = Seq(
  organization := "me.jooohn",
  version := "0.0.1",
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq(
    "-Ypartial-unification"
  ),
  libraryDependencies
    ++= Seq(Cats.core)
    ++ Shapeless.all
    ++ Logging.all
    ++ TestDependencies.all,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases")
  ),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
)
lazy val stayed = (project in file("."))
  .settings(baseSettings)
  .settings(
    name := "stayed",
    libraryDependencies ++= Seq(Config.config)
  )
  .dependsOn(adapter % "test->test;compile->compile")

lazy val domain = (project in file("domain"))
  .settings(baseSettings)
  .settings(
    name := "stayed-domain"
  )
  .dependsOn(infrastructure % "test->test;compile->compile")

lazy val adapter = (project in file("adapter"))
  .settings(baseSettings)
  .settings(
    name := "stayed-adapter",
    libraryDependencies ++= Doobie.all ++ Circe.all ++ Http4s.all ++ Firebase.all
  )
  .dependsOn(`use-case` % "test->test;compile->compile")

lazy val `use-case` = (project in file("use-case"))
  .settings(baseSettings)
  .settings(
    name := "stayed-use-case"
  )
  .dependsOn(domain % "test->test;compile->compile")

lazy val `infrastructure` = (project in file("infrastructure"))
  .settings(baseSettings)
  .settings(
    name := "stayed-infrastructure"
  )
