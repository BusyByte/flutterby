val scalaV = "3.0.0-RC1"

val catsEffectV = "3.0.2"

val flywayV     = "7.11.2"

// Test
val specs2V                 = "4.12.0"

val testContainersScalaV    = "0.39.4" // https://github.com/testcontainers/testcontainers-scala/releases

val testContainersPostgresV = "1.15.3" // https://github.com/testcontainers/testcontainers-java/releases

val postgresV               = "42.2.20"


lazy val `flutterby` =
  (project in file("."))
    .aggregate(`flutterby-core`, `flutterby-cats`)
    .settings(noPublishSettings)
    .settings(commonSettings, releaseSettings)

lazy val `flutterby-core` = project
  .in(file("modules/core"))
  .settings(name := "flutterby-core")
  .settings(commonSettings, releaseSettings)

lazy val `flutterby-cats` = project
  .in(file("modules/cats"))
  .dependsOn(`flutterby-core`)
  .settings(name := "flutterby-cats")
  .settings(commonSettings, releaseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel"     %% "cats-effect"          % catsEffectV,
      "com.dimafeng"      %% "testcontainers-scala" % testContainersScalaV    % Test,
      "org.testcontainers" % "postgresql"           % testContainersPostgresV % Test,
      "org.postgresql"     % "postgresql"           % postgresV               % Test
    )
  )

lazy val commonSettings = Seq(
  organization := "dev.shawngarner",
  scalaVersion := scalaV ,
//  scalacOptions ++= Seq(
//    "-rewrite",
//    "-new-syntax",
//    "-source:3.0-migration"
//  ),
  libraryDependencies ++= Seq(
    "org.flywaydb" % "flyway-core"       % flywayV,
    ("org.specs2"  %% "specs2-core"       % specs2V % Test).cross(CrossVersion.for3Use2_13),
    ("org.specs2"  %% "specs2-scalacheck" % specs2V % Test).cross(CrossVersion.for3Use2_13)
  )
)

lazy val contributors = Seq(
  "BusyByte" -> "Shawn Garner"
)

lazy val releaseSettings = Seq(
  Test / publishArtifact := false,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/BusyByte/flutterby"),
      "git@github.com:BusyByte/flutterby.git"
    )
  ),
  homepage := Some(url("https://github.com/BusyByte/flutterby")),
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra := {
    <developers>
      {
      for ((username, name) <- contributors)
        yield <developer>
      <id>{username}</id>
      <name>{name}</name>
      <url>http://github.com/{username}</url>
    </developer>
    }
    </developers>
  }
)

lazy val noPublishSettings =
  Seq(
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
