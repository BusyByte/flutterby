import Dependencies.Libraries

val scala2_12V = "2.12.11"
val scala2_13V = "2.13.2"

val silencerV = "1.7.0"

name := """flutterby"""

organization in ThisBuild := "dev.shawngarner"

scalaVersion in ThisBuild := scala2_12V

// check for library updates whenever the project is [re]load
onLoad in Global := { s =>
  "dependencyUpdates" :: s
}

lazy val commonSettings = Seq(
  organizationName := "dev.busybyte",
  //scalafmtOnCompile := true,
  libraryDependencies ++= Seq(
    Libraries.flyway,
    Libraries.specs2           % Test,
    Libraries.specs2ScalaCheck % Test,
    compilerPlugin(Libraries.kindProjector),
    compilerPlugin(Libraries.betterMonadicFor)
  )
)

lazy val `flutterby`    =
  (project in file("."))
    .aggregate(`flutterby-core`, `flutterby-cats_1_x`, `flutterby-cats_2_x`)

lazy val `flutterby-core`     = project
  .in(file("modules/core"))
  .settings(name := "flutterby-core")
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerV cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerV % Provided cross CrossVersion.full
    )
  )
  .settings(crossScalaVersions := Seq(scala2_12V, scala2_13V))

lazy val `flutterby-cats_1_x` = project
  .in(file("modules/cats_1_x"))
  .dependsOn(`flutterby-core`)
  .settings(name := "flutterby-cats_1_x")
  .settings(crossScalaVersions := Seq(scala2_12V))
  .settings(commonSettings: _*)
  .settings(libraryDependencies += Libraries.catsEffect_1_x)

lazy val `flutterby-cats_2_x` = project
  .in(file("modules/cats_2_x"))
  .dependsOn(`flutterby-core`)
  .settings(name := "flutterby-cats_2_x")
  .settings(crossScalaVersions := Seq(scala2_12V, scala2_13V))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Libraries.catsEffect_2_x,
      Libraries.testContainersScala    % Test,
      Libraries.testContainersPostgres % Test,
      Libraries.postgres               % Test
    )
  )
