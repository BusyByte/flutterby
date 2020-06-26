[![Build Status](https://travis-ci.org/BusyByte/flutterby.svg?branch=master)](https://travis-ci.org/BusyByte/flutterby) 
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.shawngarner/flutterby-core_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/dev.shawngarner/flutterby-core_2.12)

# flutterby

FP Scala Wrapper for Flyway

## Installation

Add one of the following to libraryDependencies in build.sbt.

### Cats Effect 1.x

Supports Scala 2.12.x

```sbt
"dev.shawngarner" %% "flutterby-cats_1_x" % "<release-version>"
```

### Cats Effect 2.x

Supports Scala 2.12.x and 2.13.x

```sbt
"dev.shawngarner" %% "flutterby-cats_2_x" % "<release-version>"
```

## Basic Usage

```scala
import cats.effect.IO
import cats.implicits._
import flutterby.core.Flutterby
import flutterby.cats.config.ConfigBuilder
import flutterby.cats.syntax.all._

val flutterby: IO[Flutterby[IO]] =
    ConfigBuilder
      .impl[IO]
      .dataSource(jdbcUrl, dbUserName, dbPassword)
      .load

for {
    fb                                <- flutterby
    successfullyAppliedMigrationCount <- fb.migrate()
    infoAfterMigrate                  <- fb.info()
} yield (successfullyAppliedMigrationCount, infoAfterMigrate)
```
