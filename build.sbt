import Dependencies.Libraries

name := """flutterby"""

organization in ThisBuild := "dev.busybyte"

scalaVersion in ThisBuild := "2.12.10"


lazy val commonSettings = Seq(
  organizationName := "dev.busybyte",
  scalafmtOnCompile := true,
  libraryDependencies ++= Seq(
    Libraries.flyway,
    Libraries.specs2  % Test,
    Libraries.specs2ScalaCheck % Test,
    compilerPlugin(Libraries.kindProjector),
    compilerPlugin(Libraries.betterMonadicFor)
  )
)

lazy val `flutterby` =
  (project in file("."))
    .aggregate(`flutterby-core`, `flutterby-cats_1_x`)

lazy val `flutterby-core` = project
  .in(file("modules/core"))
  .settings(commonSettings: _*)
  .settings(crossScalaVersions := Seq("2.12.10", "2.13.1"))

lazy val `flutterby-cats_1_x` = project
  .in(file("modules/cats_1_x"))
  .dependsOn(`flutterby-core`)
  .settings(commonSettings: _*)
  .settings(libraryDependencies += Libraries.catsEffect_1_x)
  .settings(crossScalaVersions := Seq("2.12.10"))
