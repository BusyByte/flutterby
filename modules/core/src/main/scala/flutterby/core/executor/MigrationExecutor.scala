package flutterby.core.executor

import java.sql.Connection

import flutterby.core.config.FlutterbyConfig
import org.flywaydb.core.api.executor.{ MigrationExecutor => FlywayMigrationExecutor, Context => FlywayContext }

import scala.util.Try

final case class Context(
    configuration: FlutterbyConfig,
    connection: Connection
)

trait MigrationExecutor { //TODO: boolean blindness

  /**
    * If there is an exception in the try it will
    * end up being thrown in the Flyway MigrationExecutor
    */
  def execute(context: Context): Try[Unit]
  def canExecuteInTransaction: Boolean
}
object MigrationExecutor {
  def toFlyway(m: MigrationExecutor): FlywayMigrationExecutor = new FlywayMigrationExecutor {
    override def execute(context: FlywayContext): Unit =
      m.execute(
          Context(FlutterbyConfig.fromFlyway(context.getConfiguration), context.getConnection)
        )
        .get
    override def canExecuteInTransaction(): Boolean = m.canExecuteInTransaction
  }
}
