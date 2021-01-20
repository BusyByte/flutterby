import sbt._

object Dependencies {

  object Versions {
    val catsEffect_2_x = "2.3.1"
    val catsEffect_1_x = "1.4.0"

    // Test
    val specs2                        = "4.10.6"
    val testContainersScalaVersion    = "0.38.8" // https://github.com/testcontainers/testcontainers-scala/releases
    val testContainersPostgresVersion = "1.15.1" // https://github.com/testcontainers/testcontainers-java/releases
    val postgresVersion               = "42.2.18"

    // Compiler
    val kindProjector    = "0.10.3"
    val betterMonadicFor = "0.3.1"
    val flyway           = "6.5.7"
  }

  object Libraries {
    lazy val flyway         = "org.flywaydb"   % "flyway-core" % Versions.flyway
    lazy val catsEffect_1_x = "org.typelevel" %% "cats-effect" % Versions.catsEffect_1_x
    lazy val catsEffect_2_x = "org.typelevel" %% "cats-effect" % Versions.catsEffect_2_x

    // Test
    lazy val specs2           = "org.specs2" %% "specs2-core"       % Versions.specs2
    lazy val specs2ScalaCheck = "org.specs2" %% "specs2-scalacheck" % Versions.specs2

    lazy val testContainersScala    = "com.dimafeng"      %% "testcontainers-scala" % Versions.testContainersScalaVersion
    lazy val testContainersPostgres = "org.testcontainers" % "postgresql"           % Versions.testContainersPostgresVersion
    lazy val postgres               = "org.postgresql"     % "postgresql"           % Versions.postgresVersion

    // Compiler
    lazy val kindProjector    = "org.typelevel" %% "kind-projector"     % Versions.kindProjector
    lazy val betterMonadicFor = "com.olegpy"    %% "better-monadic-for" % Versions.betterMonadicFor
  }

}
