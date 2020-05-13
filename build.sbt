import Dependencies.Libraries

val scala2_12V = "2.12.11"
val scala2_13V = "2.13.2"

scalaVersion in ThisBuild := scala2_12V

publishTo in ThisBuild := sonatypePublishToBundle.value

// check for library updates whenever the project is [re]load
onLoad in Global := { s =>
  "dependencyUpdates" :: s
}

lazy val `flutterby` =
  (project in file("."))
    .aggregate(`flutterby-core`, `flutterby-cats_1_x`, `flutterby-cats_2_x`)
    .settings(noPublishSettings)
    .settings(commonSettings, releaseSettings)

lazy val `flutterby-core` = project
  .in(file("modules/core"))
  .settings(name := "flutterby-core")
  .settings(commonSettings, releaseSettings)
  .settings(crossScalaVersions := Seq(scala2_12V, scala2_13V))

lazy val `flutterby-cats_1_x` = project
  .in(file("modules/cats_1_x"))
  .dependsOn(`flutterby-core`)
  .settings(name := "flutterby-cats_1_x")
  .settings(crossScalaVersions := Seq(scala2_12V))
  .settings(commonSettings, releaseSettings)
  .settings(libraryDependencies += Libraries.catsEffect_1_x)

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

lazy val commonSettings       = Seq(
  organization := "dev.shawngarner",
  libraryDependencies ++= Seq(
    Libraries.flyway,
    Libraries.specs2           % Test,
    Libraries.specs2ScalaCheck % Test,
    compilerPlugin(Libraries.kindProjector),
    compilerPlugin(Libraries.betterMonadicFor)
  )
)

import ReleaseTransformations._

lazy val contributors         = Seq(
  "BusyByte" -> "Shawn Garner"
)

lazy val releaseSettings      = Seq(
  publishArtifact in Test := false,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/BusyByte/flutterby"),
      "git@github.com:BusyByte/flutterby.git"
    )
  ),
  homepage := Some(url("https://github.com/BusyByte/flutterby")),
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra := {
    <developers>
      {
      for ((username, name) <- contributors) yield <developer>
      <id>{username}</id>
      <name>{name}</name>
      <url>http://github.com/{username}</url>
    </developer>
    }
    </developers>
  },
  releaseCrossBuild := true, // true if you cross-build the project for multiple Scala versions
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies, // : ReleaseStep
    inquireVersions,           // : ReleaseStep
    runClean,                  // : ReleaseStep
    runTest,                   // : ReleaseStep
    setReleaseVersion,         // : ReleaseStep
    commitReleaseVersion,      // : ReleaseStep, performs the initial git checks
    tagRelease,                // : ReleaseStep
    // For non cross-build projects, use releaseStepCommand("publishSigned")
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion,            // : ReleaseStep
    commitNextVersion,         // : ReleaseStep
    pushChanges                // : ReleaseStep, also checks that an upstream branch is properly configured
  )
)

lazy val noPublishSettings    =
  Seq(
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
