package flutterby.core

import org.flywaydb.core.api.{ MigrationVersion => FlywayMigrationVersion }
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class MigrationVersionSpec extends Specification with ScalaCheck {
  "EMPTY is equivalent using fromFlyway" in {
    val f = FlywayMigrationVersion.EMPTY
    val m = MigrationVersion.fromFlyway(f)
    m ==== MigrationVersion.EMPTY
    m.displayText.value ==== f.toString
    m.version.map(_.value) ==== Option(f.getVersion)
  }

  "EMPTY is equivalent using toFlyway" in {
    val m = MigrationVersion.EMPTY
    val f = m.toFlyway
    f ==== FlywayMigrationVersion.EMPTY
    f.toString ==== m.displayText.value
    Option(f.getVersion) ==== m.version.map(_.value)
  }

  "LATEST is equivalent using fromFlyway" in {
    val f = FlywayMigrationVersion.LATEST
    val m = MigrationVersion.fromFlyway(f)
    m ==== MigrationVersion.LATEST
    m.displayText.value ==== f.toString
    m.version.map(_.value) ==== Option(f.getVersion)
  }

  "LATEST is equivalent using toFlyway" in {
    val m = MigrationVersion.LATEST
    val f = m.toFlyway
    f ==== FlywayMigrationVersion.LATEST
    f.toString ==== m.displayText.value
    Option(f.getVersion) ==== m.version.map(_.value)
  }

  "CURRENT is equivalent using fromFlyway" in {
    val f = FlywayMigrationVersion.CURRENT
    val m = MigrationVersion.fromFlyway(f)
    m ==== MigrationVersion.CURRENT
    m.displayText.value ==== f.toString
    m.version.map(_.value) ==== Option(f.getVersion)
  }

  "CURRENT is equivalent using toFlyway" in {
    val m = MigrationVersion.CURRENT
    val f = m.toFlyway
    f ==== FlywayMigrationVersion.CURRENT
    f.toString ==== m.displayText.value
    Option(f.getVersion) ==== m.version.map(_.value)
  }

  {
    import MigrationVersionArbitraries.arbFlywayMigrationVersion
    "equivalent using fromFlyway" in prop { f: FlywayMigrationVersion =>
      val m = MigrationVersion.fromFlyway(f)
      m.displayText.value ==== f.toString
      m.version.map(_.value) ==== Option(f.getVersion)
    }
  }

  {
    import MigrationVersionArbitraries.arbMigrationVersion
    "equivalent using toFlyway" in prop { m: MigrationVersion =>
      val f = m.toFlyway
      f.toString ==== m.displayText.value
      Option(f.getVersion) ==== m.version.map(_.value)
    }
  }
}
