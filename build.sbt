val catsEffectV = "3.0.0-RC1"
val flywayV     = "7.5.3"

// Test
val specs2V                 = "4.10.6"
val testContainersScalaV    = "0.39.1" // https://github.com/testcontainers/testcontainers-scala/releases
val testContainersPostgresV = "1.15.2" // https://github.com/testcontainers/testcontainers-java/releases
val postgresV               = "42.2.18"

// Compiler
//val kindProjectorV    = "0.10.3"
//val betterMonadicForV = "0.3.1"

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
  scalaVersion := "3.0.0-M3",
//  scalacOptions ++= Seq(
//    "-rewrite",
//    "-new-syntax",
//    "-source:3.0-migration"
//  ),
  libraryDependencies ++= Seq(
    "org.flywaydb" % "flyway-core"       % flywayV,
    ("org.specs2"  %% "specs2-core"       % specs2V % Test).withDottyCompat(scalaVersion.value),
    ("org.specs2"  %% "specs2-scalacheck" % specs2V % Test).withDottyCompat(scalaVersion.value)
    //compilerPlugin("org.typelevel" %% "kind-projector"     % kindProjectorV),
    //compilerPlugin("com.olegpy"    %% "better-monadic-for" % betterMonadicForV)
  )
)

lazy val contributors = Seq(
  "BusyByte" -> "Shawn Garner"
)

lazy val releaseSettings = Seq(
  publishArtifact in Test := false,
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
