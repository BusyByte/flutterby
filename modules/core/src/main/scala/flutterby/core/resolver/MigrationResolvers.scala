package flutterby.core.resolver

import java.sql.Connection
import java.util

import flutterby.core.config.FlutterbyConfig
import flutterby.core.jdk.CollectionConversions
import flutterby.core.{ Checksum, Description, MigrationType, MigrationVersion, Script }
import org.flywaydb.core.api
import org.flywaydb.core.api.resolver.{
  MigrationExecutor => FlywayMigrationExecutor,
  MigrationResolver => FlywayMigrationResolver,
  ResolvedMigration => FlywayResolvedMigration
}

import scala.util.Try

sealed trait FlutterbyMigrationResolver
object FlutterbyMigrationResolver {

  final case class ConfigAwareMigrationResolver(f: FlutterbyConfig => MigrationResolver)
      extends FlutterbyMigrationResolver

  trait MigrationResolver extends FlutterbyMigrationResolver {
    def resolveMigrations: Iterable[ResolvedMigration]
  }

  object MigrationResolver {

    def toFlyway(m: MigrationResolver): FlywayMigrationResolver = new FlywayMigrationResolver {
      override def resolveMigrations(): util.Collection[FlywayResolvedMigration] =
        CollectionConversions.toJavaCollection(m.resolveMigrations.map(ResolvedMigration.toFlyway))
    }
  }

  def toFlyway(m: FlutterbyMigrationResolver, flutterbyConfig: FlutterbyConfig): FlywayMigrationResolver = m match {
    case r: MigrationResolver             => MigrationResolver.toFlyway(r)
    case ConfigAwareMigrationResolver(fn) => MigrationResolver.toFlyway(fn(flutterbyConfig))
  }

}

final case class PhysicalLocation(value: String) extends AnyVal
trait ResolvedMigration {
  def getVersion: MigrationVersion
  def getDescription: Description
  def getScript: Script
  def getChecksum: Checksum
  def getType: MigrationType
  def getPhysicalLocation: PhysicalLocation
  def getExecutor: MigrationExecutor
}
object ResolvedMigration {
  def toFlyway(r: ResolvedMigration): FlywayResolvedMigration = new FlywayResolvedMigration {
    override def getVersion: api.MigrationVersion     = MigrationVersion.toFlyway(r.getVersion)
    override def getDescription: String               = r.getDescription.value
    override def getScript: String                    = r.getScript.value
    override def getChecksum: Integer                 = r.getChecksum.value
    override def getType: api.MigrationType           = MigrationType.toFlyway(r.getType)
    override def getPhysicalLocation: String          = r.getPhysicalLocation.value
    override def getExecutor: FlywayMigrationExecutor = MigrationExecutor.toFlyway(r.getExecutor)
  }
}

trait MigrationExecutor {

  /**
    * If there is an exception in the try it will
    * end up being thrown in the Flyway MigrationExecutor
    */
  def execute(connection: Connection): Try[Unit]
  def executeInTransaction: Boolean
}
object MigrationExecutor {
  def toFlyway(m: MigrationExecutor): FlywayMigrationExecutor = new FlywayMigrationExecutor {
    override def execute(connection: Connection): Unit = m.execute(connection).get
    override def executeInTransaction(): Boolean       = m.executeInTransaction
  }
}

final case class MigrationResolvers(resolvers: Vector[FlutterbyMigrationResolver])
