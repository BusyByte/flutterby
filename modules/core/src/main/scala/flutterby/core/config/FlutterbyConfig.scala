package flutterby.core.config

import java.io.OutputStream
import java.util

import flutterby.core.MigrationVersion.NormalMigrationVersion
import flutterby.core.jdk.CollectionConversions
import flutterby.core.{ DisplayText, MigrationVersion }
import javax.sql.DataSource
import org.flywaydb.core.api
import org.flywaydb.core.api.callback.FlywayCallback
import org.flywaydb.core.api.configuration.FlywayConfiguration
import org.flywaydb.core.api.errorhandler.ErrorHandler
import org.flywaydb.core.api.resolver.MigrationResolver

final case class FlutterbyConfig(
    classLoader: ClassLoader,
    dataSource: Option[DataSource],
    baselineVersion: MigrationVersion,
    baselineDescription: String,
    resolvers: Vector[MigrationResolver],
    skipDefaultResolvers: Boolean,
    callbacks: Vector[FlywayCallback],
    skipDefaultCallbacks: Boolean,
    sqlMigrationPrefix: String,
    undoSqlMigrationPrefix: String,
    repeatableSqlMigrationPrefix: String,
    sqlMigrationSeparator: String,
    sqlMigrationSuffixes: Vector[String],
    placeholderReplacement: Boolean,
    placeholderSuffix: String,
    placeholderPrefix: String,
    placeholders: Map[String, String],
    target: Option[MigrationVersion],
    table: String,
    schemas: Vector[String],
    encoding: String,
    locations: Vector[String],
    baselineOnMigrate: Boolean,
    outOfOrder: Boolean,
    ignoreMissingMigrations: Boolean,
    ignoreFutureMigrations: Boolean,
    validateOnMigrate: Boolean,
    cleanOnValidationError: Boolean,
    cleanDisabled: Boolean,
    mixed: Boolean,
    group: Boolean,
    installedBy: Option[String],
    errorHandlers: Vector[ErrorHandler],
    dryRunOutput: Option[OutputStream]
)

object FlutterbyConfig {
  val default = FlutterbyConfig(
    classLoader = Thread.currentThread.getContextClassLoader,
    dataSource = None,
    baselineVersion = NormalMigrationVersion(DisplayText("1")),
    baselineDescription = "<< Flyway Baseline >>",
    resolvers = Vector.empty,
    skipDefaultResolvers = false,
    callbacks = Vector.empty,
    skipDefaultCallbacks = false,
    sqlMigrationPrefix = "V",
    undoSqlMigrationPrefix = "U",
    repeatableSqlMigrationPrefix = "R",
    sqlMigrationSeparator = "__",
    sqlMigrationSuffixes = Vector(".sql"),
    placeholderReplacement = true,
    placeholderSuffix = "}",
    placeholderPrefix = "${",
    placeholders = Map.empty,
    target = None,
    table = "flyway_schema_history",
    schemas = Vector.empty,
    encoding = "UTF-8",
    locations = Vector("db/migration"),
    baselineOnMigrate = false,
    outOfOrder = false,
    ignoreMissingMigrations = false,
    ignoreFutureMigrations = true,
    validateOnMigrate = true,
    cleanOnValidationError = false,
    cleanDisabled = false,
    mixed = false,
    group = false,
    installedBy = None,
    errorHandlers = Vector.empty,
    dryRunOutput = None
  )

  def toFlyway(c: FlutterbyConfig): FlywayConfiguration = new FlywayConfiguration {

    override def getClassLoader: ClassLoader               = c.classLoader
    override def getDataSource: DataSource                 = c.dataSource.orNull
    override def getBaselineVersion: api.MigrationVersion  = c.baselineVersion.toFlyway
    override def getBaselineDescription: String            = c.baselineDescription
    override def getResolvers: Array[MigrationResolver]    = c.resolvers.toArray
    override def isSkipDefaultResolvers: Boolean           = c.skipDefaultResolvers
    override def getCallbacks: Array[FlywayCallback]       = c.callbacks.toArray
    override def isSkipDefaultCallbacks: Boolean           = c.skipDefaultCallbacks
    override def getSqlMigrationPrefix: String             = c.sqlMigrationPrefix
    override def getUndoSqlMigrationPrefix: String         = c.undoSqlMigrationPrefix
    override def getRepeatableSqlMigrationPrefix: String   = c.repeatableSqlMigrationPrefix
    override def getSqlMigrationSeparator: String          = c.sqlMigrationSeparator
    override def getSqlMigrationSuffix: String             = c.sqlMigrationSuffixes.headOption.orNull
    override def getSqlMigrationSuffixes: Array[String]    = c.sqlMigrationSuffixes.toArray
    override def isPlaceholderReplacement: Boolean         = c.placeholderReplacement
    override def getPlaceholderSuffix: String              = c.placeholderSuffix
    override def getPlaceholderPrefix: String              = c.placeholderPrefix
    override def getPlaceholders: util.Map[String, String] = CollectionConversions.toJavaMap(c.placeholders)
    override def getTarget: api.MigrationVersion           = c.baselineVersion.toFlyway
    override def getTable: String                          = c.table
    override def getSchemas: Array[String]                 = c.schemas.toArray
    override def getEncoding: String                       = c.encoding
    override def getLocations: Array[String]               = c.locations.toArray
    override def isBaselineOnMigrate: Boolean              = c.baselineOnMigrate
    override def isOutOfOrder: Boolean                     = c.outOfOrder
    override def isIgnoreMissingMigrations: Boolean        = c.ignoreMissingMigrations
    override def isIgnoreFutureMigrations: Boolean         = c.ignoreFutureMigrations
    override def isValidateOnMigrate: Boolean              = c.validateOnMigrate
    override def isCleanOnValidationError: Boolean         = c.cleanOnValidationError
    override def isCleanDisabled: Boolean                  = c.cleanDisabled
    override def isMixed: Boolean                          = c.mixed
    override def isGroup: Boolean                          = c.group
    override def getInstalledBy: String                    = c.installedBy.orNull
    override def getErrorHandlers: Array[ErrorHandler]     = c.errorHandlers.toArray
    override def getDryRunOutput: OutputStream             = c.dryRunOutput.orNull
  }

  implicit class FlutterbyConfigOps(val c: FlutterbyConfig) extends AnyVal {
    def toFlyway: FlywayConfiguration = FlutterbyConfig.toFlyway(c)
  }
}
