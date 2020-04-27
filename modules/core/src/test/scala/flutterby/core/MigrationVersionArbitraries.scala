package flutterby.core

import org.flywaydb.core.api.{ MigrationVersion => FlywayMigrationVersion }
import org.scalacheck.{ Arbitrary, Gen }

object MigrationVersionArbitraries {
  implicit val arbMigrationVersion: Arbitrary[MigrationVersion] = Arbitrary {
    for {
      empty <- Gen.const(MigrationVersion.EMPTY)
      latest <- Gen.const(MigrationVersion.LATEST)
      current <- Gen.const(MigrationVersion.CURRENT)
      normal <- Gen
                 .oneOf("6", "6.0", "005", "1.2.3.4", "201004200021")
                 .map(v => MigrationVersion.fromVersionString(Some(v)))
      result <- Gen.oneOf(empty, latest, current, normal)
    } yield result
  }

  implicit val arbFlywayMigrationVersion: Arbitrary[FlywayMigrationVersion] = Arbitrary {
    for {
      empty <- Gen.const(FlywayMigrationVersion.EMPTY)
      latest <- Gen.const(FlywayMigrationVersion.LATEST)
      current <- Gen.const(FlywayMigrationVersion.CURRENT)
      normal <- Gen
                 .oneOf("6", "6.0", "005", "1.2.3.4", "201004200021")
                 .map(v => FlywayMigrationVersion.fromVersion(v))
      result <- Gen.oneOf(empty, latest, current, normal)
    } yield result
  }
}
