package flutterby.core.callback

import java.sql.Connection

import flutterby.core.MigrationInfo
import flutterby.core.config.FlutterbyConfig
import org.flywaydb.core.api.callback.{
  Warning => FlywayWarning,
  Error => FlywayError,
  Statement => FlywayStatement,
  Callback => FWCallback,
  Context => FlywayContext,
  Event => FlywayEvent
}

trait Callback         {
  def supports(event: Event, context: Context): Boolean
  def canHandleInTransaction(event: Event, context: Context): Boolean
  def handle(event: Event, context: Context): Unit
}
object Callback        {
  def toFlyway(callback: Callback): FWCallback =
    new FWCallback {
      override def supports(event: FlywayEvent, context: FlywayContext): Boolean               =
        callback.supports(Event.fromFlyway(event), Context.fromFlyway(context))
      override def canHandleInTransaction(event: FlywayEvent, context: FlywayContext): Boolean =
        callback.canHandleInTransaction(Event.fromFlyway(event), Context.fromFlyway(context))
      override def handle(event: FlywayEvent, context: FlywayContext): Unit                    =
        callback.handle(Event.fromFlyway(event), Context.fromFlyway(context))
    }
}

trait Context          {
  def configuration: FlutterbyConfig
  def connection: Connection
  def migrationInfo: MigrationInfo
  def statement: Statement
}
object Context         {
  def fromFlyway(f: FlywayContext): Context =
    new Context {
      override def configuration: FlutterbyConfig = FlutterbyConfig.fromFlyway(f.getConfiguration)
      override def connection: Connection         = f.getConnection
      override def migrationInfo: MigrationInfo   = MigrationInfo.fromFlyway(f.getMigrationInfo)
      override def statement: Statement           = Statement.fromFlyway(f.getStatement)
    }
}

final case class Sql(value: String)     extends AnyVal
sealed trait Statement {
  def getSql: Sql
  def getWarnings: List[Warning]
  def getErrors: List[Error]
}
object Statement       {
  def fromFlyway(f: FlywayStatement): Statement = ??? // TODO implement
}

sealed trait `IsHandled?`
object `IsHandled?`    {
  case object Handled    extends `IsHandled?`
  case object NotHandled extends `IsHandled?`
}

final case class Code(value: Int)       extends AnyVal
final case class State(value: String)   extends AnyVal
final case class Message(value: String) extends AnyVal
trait Warning          {
  def code: Code
  def state: State
  def message: Message
  def handled: `IsHandled?`
  def markHandled(handled: `IsHandled?`): Unit
}
object Warning         {
  def toFlyway(e: Warning): FlywayWarning   = ??? // TODO: implement
  def fromFlyway(f: FlywayWarning): Warning = ??? // TODO: implement
}

trait Error            {
  def code: Code
  def state: State
  def message: Message
  def handled: `IsHandled?`
  def markHandled(handled: `IsHandled?`): Unit
}

object Error {
  def toFlyway(e: Error): FlywayError   = ??? // TODO: implement
  def fromFlyway(f: FlywayError): Error = ??? // TODO: implement
}

sealed abstract class Event(val id: String)
object Event {
  case object BEFORE_CLEAN                       extends Event("beforeClean")
  case object AFTER_CLEAN                        extends Event("afterClean")
  case object AFTER_CLEAN_ERROR                  extends Event("afterCleanError")
  case object BEFORE_MIGRATE                     extends Event("beforeMigrate")
  case object BEFORE_EACH_MIGRATE                extends Event("beforeEachMigrate")
  case object BEFORE_EACH_MIGRATE_STATEMENT      extends Event("beforeEachMigrateStatement")
  case object AFTER_EACH_MIGRATE_STATEMENT       extends Event("afterEachMigrateStatement")
  case object AFTER_EACH_MIGRATE_STATEMENT_ERROR extends Event("afterEachMigrateStatementError")
  case object AFTER_EACH_MIGRATE                 extends Event("afterEachMigrate")
  case object AFTER_EACH_MIGRATE_ERROR           extends Event("afterEachMigrateError")
  case object AFTER_MIGRATE                      extends Event("afterMigrate")
  case object AFTER_MIGRATE_ERROR                extends Event("afterMigrateError")
  case object BEFORE_UNDO                        extends Event("beforeUndo")
  case object BEFORE_EACH_UNDO                   extends Event("beforeEachUndo")
  case object BEFORE_EACH_UNDO_STATEMENT         extends Event("beforeEachUndoStatement")
  case object AFTER_EACH_UNDO_STATEMENT          extends Event("afterEachUndoStatement")
  case object AFTER_EACH_UNDO_STATEMENT_ERROR    extends Event("afterEachUndoStatementError")
  case object AFTER_EACH_UNDO                    extends Event("afterEachUndo")
  case object AFTER_EACH_UNDO_ERROR              extends Event("afterEachUndoError")
  case object AFTER_UNDO                         extends Event("afterUndo")
  case object AFTER_UNDO_ERROR                   extends Event("afterUndoError")
  case object BEFORE_VALIDATE                    extends Event("beforeValidate")
  case object AFTER_VALIDATE                     extends Event("afterValidate")
  case object AFTER_VALIDATE_ERROR               extends Event("afterValidateError")
  case object BEFORE_BASELINE                    extends Event("beforeBaseline")
  case object AFTER_BASELINE                     extends Event("afterBaseline")
  case object AFTER_BASELINE_ERROR               extends Event("afterBaselineError")
  case object BEFORE_REPAIR                      extends Event("beforeRepair")
  case object AFTER_REPAIR                       extends Event("afterRepair")
  case object AFTER_REPAIR_ERROR                 extends Event("afterRepairError")
  case object BEFORE_INFO                        extends Event("beforeInfo")
  case object AFTER_INFO                         extends Event("afterInfo")
  case object AFTER_INFO_ERROR                   extends Event("afterInfoError")

  def fromFlyway(f: FlywayEvent): Event = ??? // TODO implement
  def toFlyway(e: Event): FlywayEvent   = ??? // TODO implement
}

final case class Callbacks(callbacks: Vector[Callback])
