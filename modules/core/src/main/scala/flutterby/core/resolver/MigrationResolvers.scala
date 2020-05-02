package flutterby.core.resolver

import java.util

import flutterby.core.config.FlutterbyConfig
import flutterby.core.executor.MigrationExecutor
import flutterby.core.jdk.CollectionConversions
import flutterby.core.{Checksum, Description, MigrationType, MigrationVersion, Script}
import org.flywaydb.core.api
import org.flywaydb.core.api.resolver.{
  MigrationResolver => FlywayMigrationResolver,
  ResolvedMigration => FlywayResolvedMigration,
  Context => FlywayContext
}
import org.flywaydb.core.api.executor.{MigrationExecutor => FlywayMigrationExecutor}

sealed trait FlutterbyMigrationResolver
object FlutterbyMigrationResolver {

  final case class ConfigAwareMigrationResolver(f: FlutterbyConfig => MigrationResolver)
      extends FlutterbyMigrationResolver

  trait MigrationResolver extends FlutterbyMigrationResolver {
    def resolveMigrations(config: FlutterbyConfig): Iterable[ResolvedMigration]
  }

  object MigrationResolver {

    def toFlyway(m: MigrationResolver): FlywayMigrationResolver =
      new FlywayMigrationResolver {
        override def resolveMigrations(context: FlywayContext): util.Collection[FlywayResolvedMigration] =
          CollectionConversions.toJavaCollection(
            m.resolveMigrations(FlutterbyConfig.fromFlyway(context.getConfiguration)).map(ResolvedMigration.toFlyway)
          )
      }
  }

  def toFlyway(m: FlutterbyMigrationResolver, flutterbyConfig: FlutterbyConfig): FlywayMigrationResolver =
    m match {
      case r: MigrationResolver             => MigrationResolver.toFlyway(r)
      case ConfigAwareMigrationResolver(fn) => MigrationResolver.toFlyway(fn(flutterbyConfig))
    }

}

final case class PhysicalLocation(value: String) extends AnyVal
trait ResolvedMigration  {
  def version: MigrationVersion
  def description: Description
  def script: Script
  def checksum: Checksum
  def `type`: MigrationType
  def physicalLocation: PhysicalLocation
  def executor: MigrationExecutor
}
object ResolvedMigration {
  def toFlyway(r: ResolvedMigration): FlywayResolvedMigration =
    new FlywayResolvedMigration {
      override def getVersion: api.MigrationVersion     = MigrationVersion.toFlyway(r.version)
      override def getDescription: String               = r.description.value
      override def getScript: String                    = r.script.value
      override def getChecksum: Integer                 = r.checksum.value
      override def getType: api.MigrationType           = MigrationType.toFlyway(r.`type`)
      override def getPhysicalLocation: String          = r.physicalLocation.value
      override def getExecutor: FlywayMigrationExecutor = MigrationExecutor.toFlyway(r.executor)
    }
}

final case class MigrationResolvers(resolvers: Vector[FlutterbyMigrationResolver])
