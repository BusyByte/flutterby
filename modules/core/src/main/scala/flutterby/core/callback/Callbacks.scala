package flutterby.core.callback

import java.sql.Connection

import flutterby.core.MigrationInfo
import org.flywaydb.core.api
import org.flywaydb.core.api.callback.FlywayCallback

trait Callback {
  def beforeClean(connection: Connection): Unit
  def afterClean(connection: Connection): Unit
  def beforeMigrate(connection: Connection): Unit
  def afterMigrate(connection: Connection): Unit
  def beforeUndo(connection: Connection): Unit
  def beforeEachUndo(connection: Connection, info: MigrationInfo): Unit
  def afterEachUndo(connection: Connection, info: MigrationInfo): Unit
  def afterUndo(connection: Connection): Unit
  def beforeEachMigrate(connection: Connection, info: MigrationInfo): Unit
  def afterEachMigrate(connection: Connection, info: MigrationInfo): Unit
  def beforeValidate(connection: Connection): Unit
  def afterValidate(connection: Connection): Unit
  def beforeBaseline(connection: Connection): Unit
  def afterBaseline(connection: Connection): Unit
  def beforeRepair(connection: Connection): Unit
  def afterRepair(connection: Connection): Unit
  def beforeInfo(connection: Connection): Unit
  def afterInfo(connection: Connection): Unit
}
object Callback {
  def toFlyway(c: Callback): FlywayCallback = new FlywayCallback {
    override def beforeClean(connection: Connection): Unit   = c.beforeClean(connection)
    override def afterClean(connection: Connection): Unit    = c.afterClean(connection)
    override def beforeMigrate(connection: Connection): Unit = c.beforeMigrate(connection)
    override def afterMigrate(connection: Connection): Unit  = c.afterMigrate(connection)
    override def beforeUndo(connection: Connection): Unit    = c.beforeUndo(connection)
    override def beforeEachUndo(connection: Connection, info: api.MigrationInfo): Unit =
      c.beforeEachUndo(connection, MigrationInfo.fromFlyway(info))
    override def afterEachUndo(connection: Connection, info: api.MigrationInfo): Unit =
      c.afterEachUndo(connection, MigrationInfo.fromFlyway(info))
    override def afterUndo(connection: Connection): Unit = c.afterUndo(connection)
    override def beforeEachMigrate(connection: Connection, info: api.MigrationInfo): Unit =
      c.beforeEachMigrate(connection, MigrationInfo.fromFlyway(info))
    override def afterEachMigrate(connection: Connection, info: api.MigrationInfo): Unit =
      c.afterEachMigrate(connection, MigrationInfo.fromFlyway(info))
    override def beforeValidate(connection: Connection): Unit = c.beforeValidate(connection)
    override def afterValidate(connection: Connection): Unit  = c.afterValidate(connection)
    override def beforeBaseline(connection: Connection): Unit = c.beforeBaseline(connection)
    override def afterBaseline(connection: Connection): Unit  = c.afterBaseline(connection)
    override def beforeRepair(connection: Connection): Unit   = c.beforeRepair(connection)
    override def afterRepair(connection: Connection): Unit    = c.afterRepair(connection)
    override def beforeInfo(connection: Connection): Unit     = c.beforeInfo(connection)
    override def afterInfo(connection: Connection): Unit      = c.afterInfo(connection)
  }

}

trait CallbackAdapter extends Callback {
  override def beforeClean(connection: Connection): Unit                            = {}
  override def afterClean(connection: Connection): Unit                             = {}
  override def beforeMigrate(connection: Connection): Unit                          = {}
  override def afterMigrate(connection: Connection): Unit                           = {}
  override def beforeUndo(connection: Connection): Unit                             = {}
  override def beforeEachUndo(connection: Connection, info: MigrationInfo): Unit    = {}
  override def afterEachUndo(connection: Connection, info: MigrationInfo): Unit     = {}
  override def afterUndo(connection: Connection): Unit                              = {}
  override def beforeEachMigrate(connection: Connection, info: MigrationInfo): Unit = {}
  override def afterEachMigrate(connection: Connection, info: MigrationInfo): Unit  = {}
  override def beforeValidate(connection: Connection): Unit                         = {}
  override def afterValidate(connection: Connection): Unit                          = {}
  override def beforeBaseline(connection: Connection): Unit                         = {}
  override def afterBaseline(connection: Connection): Unit                          = {}
  override def beforeRepair(connection: Connection): Unit                           = {}
  override def afterRepair(connection: Connection): Unit                            = {}
  override def beforeInfo(connection: Connection): Unit                             = {}
  override def afterInfo(connection: Connection): Unit                              = {}
}

final case class Callbacks(callbacks: Vector[Callback])
