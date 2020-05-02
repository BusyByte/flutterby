package flutterby.core.callback

import java.sql.Connection

import flutterby.core.MigrationInfo
import flutterby.core.config.FlutterbyConfig
import flutterby.core.jdk.CollectionConversions
import org.flywaydb.core.api.callback.{
  Callback => FWCallback,
  Context => FlywayContext,
  Error => FlywayError,
  Event => FlywayEvent,
  Statement => FlywayStatement,
  Warning => FlywayWarning
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
  def fromFlyway(f: FlywayStatement): Statement =
    new Statement {
      override def getSql: Sql                = Sql(f.getSql)
      override def getWarnings: List[Warning] = CollectionConversions.toScalaList(f.getWarnings).map(Warning.fromFlyway)
      override def getErrors: List[Error]     = CollectionConversions.toScalaList(f.getErrors).map(Error.fromFlyway)
    }
}

sealed trait `IsHandled?`
object `IsHandled?`    {
  case object Handled    extends `IsHandled?`
  case object NotHandled extends `IsHandled?`

  def fromFlyway(handled: Boolean): `IsHandled?` = if (handled) Handled else NotHandled
  def toFlyway(handled: `IsHandled?`): Boolean   =
    handled match {
      case Handled    => true
      case NotHandled => false
    }
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
  def fromFlyway(f: FlywayWarning): Warning =
    new Warning {
      override def code: Code                               = Code(f.getCode)
      override def state: State                             = State(f.getState)
      override def message: Message                         = Message(f.getMessage)
      override def handled: `IsHandled?`                    = `IsHandled?`.fromFlyway(f.isHandled)
      override def markHandled(handled: `IsHandled?`): Unit = f.setHandled(`IsHandled?`.toFlyway(handled))
    }
}

trait Error            {
  def code: Code
  def state: State
  def message: Message
  def handled: `IsHandled?`
  def markHandled(handled: `IsHandled?`): Unit
}

object Error {
  def fromFlyway(f: FlywayError): Error =
    new Error {
      override def code: Code                               = Code(f.getCode)
      override def state: State                             = State(f.getState)
      override def message: Message                         = Message(f.getMessage)
      override def handled: `IsHandled?`                    = `IsHandled?`.fromFlyway(f.isHandled)
      override def markHandled(handled: `IsHandled?`): Unit = f.setHandled(`IsHandled?`.toFlyway(handled))
    }
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

  def fromFlyway(f: FlywayEvent): Event =
    f match {
      case FlywayEvent.BEFORE_CLEAN                       => BEFORE_CLEAN
      case FlywayEvent.AFTER_CLEAN                        => AFTER_CLEAN
      case FlywayEvent.AFTER_CLEAN_ERROR                  => AFTER_CLEAN_ERROR
      case FlywayEvent.BEFORE_MIGRATE                     => BEFORE_MIGRATE
      case FlywayEvent.BEFORE_EACH_MIGRATE                => BEFORE_EACH_MIGRATE
      case FlywayEvent.BEFORE_EACH_MIGRATE_STATEMENT      => BEFORE_EACH_MIGRATE_STATEMENT
      case FlywayEvent.AFTER_EACH_MIGRATE_STATEMENT       => AFTER_EACH_MIGRATE_STATEMENT
      case FlywayEvent.AFTER_EACH_MIGRATE_STATEMENT_ERROR => AFTER_EACH_MIGRATE_STATEMENT_ERROR
      case FlywayEvent.AFTER_EACH_MIGRATE                 => AFTER_EACH_MIGRATE
      case FlywayEvent.AFTER_EACH_MIGRATE_ERROR           => AFTER_EACH_MIGRATE_ERROR
      case FlywayEvent.AFTER_MIGRATE                      => AFTER_MIGRATE
      case FlywayEvent.AFTER_MIGRATE_ERROR                => AFTER_MIGRATE_ERROR
      case FlywayEvent.BEFORE_UNDO                        => BEFORE_UNDO
      case FlywayEvent.BEFORE_EACH_UNDO                   => BEFORE_EACH_UNDO
      case FlywayEvent.BEFORE_EACH_UNDO_STATEMENT         => BEFORE_EACH_UNDO_STATEMENT
      case FlywayEvent.AFTER_EACH_UNDO_STATEMENT          => AFTER_EACH_UNDO_STATEMENT
      case FlywayEvent.AFTER_EACH_UNDO_STATEMENT_ERROR    => AFTER_EACH_UNDO_STATEMENT_ERROR
      case FlywayEvent.AFTER_EACH_UNDO                    => AFTER_EACH_UNDO
      case FlywayEvent.AFTER_EACH_UNDO_ERROR              => AFTER_EACH_UNDO_ERROR
      case FlywayEvent.AFTER_UNDO                         => AFTER_UNDO
      case FlywayEvent.AFTER_UNDO_ERROR                   => AFTER_UNDO_ERROR
      case FlywayEvent.BEFORE_VALIDATE                    => BEFORE_VALIDATE
      case FlywayEvent.AFTER_VALIDATE                     => AFTER_VALIDATE
      case FlywayEvent.AFTER_VALIDATE_ERROR               => AFTER_VALIDATE_ERROR
      case FlywayEvent.BEFORE_BASELINE                    => BEFORE_BASELINE
      case FlywayEvent.AFTER_BASELINE                     => AFTER_BASELINE
      case FlywayEvent.AFTER_BASELINE_ERROR               => AFTER_BASELINE_ERROR
      case FlywayEvent.BEFORE_REPAIR                      => BEFORE_REPAIR
      case FlywayEvent.AFTER_REPAIR                       => AFTER_REPAIR
      case FlywayEvent.AFTER_REPAIR_ERROR                 => AFTER_REPAIR_ERROR
      case FlywayEvent.BEFORE_INFO                        => BEFORE_INFO
      case FlywayEvent.AFTER_INFO                         => AFTER_INFO
      case FlywayEvent.AFTER_INFO_ERROR                   => AFTER_INFO_ERROR
    }
  def toFlyway(e: Event): FlywayEvent   =
    e match {
      case BEFORE_CLEAN                       => FlywayEvent.BEFORE_CLEAN
      case AFTER_CLEAN                        => FlywayEvent.AFTER_CLEAN
      case AFTER_CLEAN_ERROR                  => FlywayEvent.AFTER_CLEAN_ERROR
      case BEFORE_MIGRATE                     => FlywayEvent.BEFORE_MIGRATE
      case BEFORE_EACH_MIGRATE                => FlywayEvent.BEFORE_EACH_MIGRATE
      case BEFORE_EACH_MIGRATE_STATEMENT      => FlywayEvent.BEFORE_EACH_MIGRATE_STATEMENT
      case AFTER_EACH_MIGRATE_STATEMENT       => FlywayEvent.AFTER_EACH_MIGRATE_STATEMENT
      case AFTER_EACH_MIGRATE_STATEMENT_ERROR => FlywayEvent.AFTER_EACH_MIGRATE_STATEMENT_ERROR
      case AFTER_EACH_MIGRATE                 => FlywayEvent.AFTER_EACH_MIGRATE
      case AFTER_EACH_MIGRATE_ERROR           => FlywayEvent.AFTER_EACH_MIGRATE_ERROR
      case AFTER_MIGRATE                      => FlywayEvent.AFTER_MIGRATE
      case AFTER_MIGRATE_ERROR                => FlywayEvent.AFTER_MIGRATE_ERROR
      case BEFORE_UNDO                        => FlywayEvent.BEFORE_UNDO
      case BEFORE_EACH_UNDO                   => FlywayEvent.BEFORE_EACH_UNDO
      case BEFORE_EACH_UNDO_STATEMENT         => FlywayEvent.BEFORE_EACH_UNDO_STATEMENT
      case AFTER_EACH_UNDO_STATEMENT          => FlywayEvent.AFTER_EACH_UNDO_STATEMENT
      case AFTER_EACH_UNDO_STATEMENT_ERROR    => FlywayEvent.AFTER_EACH_UNDO_STATEMENT_ERROR
      case AFTER_EACH_UNDO                    => FlywayEvent.AFTER_EACH_UNDO
      case AFTER_EACH_UNDO_ERROR              => FlywayEvent.AFTER_EACH_UNDO_ERROR
      case AFTER_UNDO                         => FlywayEvent.AFTER_UNDO
      case AFTER_UNDO_ERROR                   => FlywayEvent.AFTER_UNDO_ERROR
      case BEFORE_VALIDATE                    => FlywayEvent.BEFORE_VALIDATE
      case AFTER_VALIDATE                     => FlywayEvent.AFTER_VALIDATE
      case AFTER_VALIDATE_ERROR               => FlywayEvent.AFTER_VALIDATE_ERROR
      case BEFORE_BASELINE                    => FlywayEvent.BEFORE_BASELINE
      case AFTER_BASELINE                     => FlywayEvent.AFTER_BASELINE
      case AFTER_BASELINE_ERROR               => FlywayEvent.AFTER_BASELINE_ERROR
      case BEFORE_REPAIR                      => FlywayEvent.BEFORE_REPAIR
      case AFTER_REPAIR                       => FlywayEvent.AFTER_REPAIR
      case AFTER_REPAIR_ERROR                 => FlywayEvent.AFTER_REPAIR_ERROR
      case BEFORE_INFO                        => FlywayEvent.BEFORE_INFO
      case AFTER_INFO                         => FlywayEvent.AFTER_INFO
      case AFTER_INFO_ERROR                   => FlywayEvent.AFTER_INFO_ERROR
    }
}

final case class Callbacks(callbacks: Vector[Callback])
