import Dependencies.Libraries

val scala2_12V = "2.12.12"
val scala2_13V = "2.13.3"

lazy val `flutterby` =
  (project in file("."))
    .aggregate(`flutterby-core`, `flutterby-cats_2_x`, `flutterby-cats_1_x`)
    .settings(noPublishSettings)
    .settings(commonSettings, releaseSettings)

lazy val `flutterby-core` = project
  .in(file("modules/core"))
  .settings(name := "flutterby-core")
  .settings(crossScalaVersions := Seq(scala2_12V, scala2_13V))
  .settings(commonSettings, releaseSettings)

lazy val `flutterby-cats_2_x` = project
  .in(file("modules/cats_2_x"))
  .dependsOn(`flutterby-core`)
  .settings(name := "flutterby-cats_2_x")
  .settings(crossScalaVersions := Seq(scala2_12V, scala2_13V))
  .settings(commonSettings, releaseSettings)
  .settings(
    libraryDependencies ++= Seq(
      Libraries.catsEffect_2_x,
      Libraries.testContainersScala    % Test,
      Libraries.testContainersPostgres % Test,
      Libraries.postgres               % Test
    )
  )

lazy val `flutterby-cats_1_x` = project
  .in(file("modules/cats_1_x"))
  .dependsOn(`flutterby-core`)
  .settings(name := "flutterby-cats_1_x")
  .settings(crossScalaVersions := Seq(scala2_12V))
  .settings(commonSettings, releaseSettings)
  .settings(libraryDependencies += Libraries.catsEffect_1_x)

lazy val commonSettings = Seq(
  organization := "dev.shawngarner",
  libraryDependencies ++= Seq(
    Libraries.flyway,
    Libraries.specs2           % Test,
    Libraries.specs2ScalaCheck % Test,
    compilerPlugin(Libraries.kindProjector),
    compilerPlugin(Libraries.betterMonadicFor)
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
