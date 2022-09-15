

# flutterby

## Status 

abandoned

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

### Compatibility

|flutterby|Flyway|Cats Effect|Scala        |Java|
|---------|------|-----------|-------------|----|
|0.1.x    |5.2.x+|1.x        |2.12.x       |1.8+|     
|0.1.x    |5.2.x+|2.x        |2.12.x/2.13.x|1.8+|
|0.2.x    |6.4.x+|1.x        |2.12.x       |1.8+|     
|0.2.x    |6.4.x+|2.x        |2.12.x/2.13.x|1.8+|



