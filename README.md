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
import flutterby.cats.FlutterbyCats
import flutterby.cats.config.{Config, ConfigBuilder}
import flutterby.cats.syntax.all._

val dbConfig: Config[IO]  = 
  ConfigBuilder
    .impl[IO]
    .dataSource(jdbcUrl, dbUserName, dbPassword)
    .build
val flutterby: IO[Flutterby[IO]] = FlutterbyCats.fromConfig[IO](dbConfig)

for {
    fb                                <- flutterby
    successfullyAppliedMigrationCount <- fb.migrate()
    infoAfterMigrate                  <- fb.info()
} yield (successfullyAppliedMigrationCount, infoAfterMigrate)
```
