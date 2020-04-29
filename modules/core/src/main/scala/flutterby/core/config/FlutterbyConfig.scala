package flutterby.core.config

import java.io.OutputStream
import java.nio.charset.{ Charset, StandardCharsets }
import java.util

import com.github.ghik.silencer.silent
import flutterby.core.jdk.CollectionConversions
import flutterby.core.MigrationVersion
import flutterby.core.resolver.{ FlutterbyMigrationResolver, MigrationResolvers }
import javax.sql.DataSource
import org.flywaydb.core.api
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.{ Configuration => FlywayConfiguration }
import org.flywaydb.core.api.errorhandler.ErrorHandler

final case class BaselineVersion(version: MigrationVersion)

final case class BaselineDescription(value: String) extends AnyVal

sealed trait `SkipDefaultResolvers?`
object `SkipDefaultResolvers?` {
  case object SkipDefaultResolvers extends `SkipDefaultResolvers?`
  case object UseDefaultResolvers extends `SkipDefaultResolvers?`

  def isSkipDefaultResolvers(s: `SkipDefaultResolvers?`): Boolean = s match {
    case SkipDefaultResolvers => true
    case UseDefaultResolvers  => false
  }

  implicit class `SkipDefaultResolvers?Ops`(val s: `SkipDefaultResolvers?`) extends AnyVal {
    def isSkipDefaultResolvers: Boolean = `SkipDefaultResolvers?`.isSkipDefaultResolvers(s)
  }
}

sealed trait `SkipDefaultCallbacks?`
object `SkipDefaultCallbacks?` {
  case object SkipDefaultCallbacks extends `SkipDefaultCallbacks?`
  case object UseDefaultCallbacks extends `SkipDefaultCallbacks?`

  def isSkipDefaultCallbacks(s: `SkipDefaultCallbacks?`): Boolean = s match {
    case SkipDefaultCallbacks => true
    case UseDefaultCallbacks  => false
  }

  implicit class `SkipDefaultCallbacks?Ops`(val s: `SkipDefaultCallbacks?`) extends AnyVal {
    def isSkipDefaultCallbacks: Boolean = `SkipDefaultCallbacks?`.isSkipDefaultCallbacks(s)
  }
}

final case class SqlMigrationPrefix(value: String) extends AnyVal

final case class UndoSqlMigrationPrefix(value: String) extends AnyVal

final case class RepeatableSqlMigrationPrefix(value: String) extends AnyVal

final case class SqlMigrationSeparator(value: String) extends AnyVal

final case class SqlMigrationSufix(value: String) extends AnyVal

final case class SqlMigrationSuffixes(sqlMigrationSuffixes: Vector[SqlMigrationSufix])

sealed trait `ReplacePlaceholders?`
object `ReplacePlaceholders?` {
  case object ReplacePlaceholders extends `ReplacePlaceholders?`
  case object DoNotReplacePlaceholders extends `ReplacePlaceholders?`

  def isPlaceholderReplacement(r: `ReplacePlaceholders?`): Boolean = r match {
    case ReplacePlaceholders      => true
    case DoNotReplacePlaceholders => false
  }

  implicit class `ReplacePlaceholders?Ops`(val r: `ReplacePlaceholders?`) extends AnyVal {
    def isPlaceholderReplacement: Boolean = `ReplacePlaceholders?`.isPlaceholderReplacement(r)
  }

}

final case class PlaceholderSuffix(value: String) extends AnyVal
final case class PlaceholderPrefix(value: String) extends AnyVal

final case class Placeholders(placeholders: Map[String, String])

final case class TargetMigrationVersion(target: MigrationVersion)

final case class SchemaHistoryTable(value: String) extends AnyVal

final case class Schemas(schemas: Vector[String])

final case class SqlMigrationEncoding(value: Charset)

final case class MigrationLocations(locations: Vector[Location])

sealed trait `BaselineOnMigrate?`
object `BaselineOnMigrate?` {
  case object Baseline extends `BaselineOnMigrate?`
  case object DoNotBaseline extends `BaselineOnMigrate?`

  def isBaselineOnMigrate(b: `BaselineOnMigrate?`): Boolean = b match {
    case Baseline      => true
    case DoNotBaseline => false
  }

  implicit class `BaselineOnMigrate?Ops`(val b: `BaselineOnMigrate?`) extends AnyVal {
    def isBaselineOnMigrate: Boolean = `BaselineOnMigrate?`.isBaselineOnMigrate(b)
  }
}

sealed trait `AllowOutOfOrder?`
object `AllowOutOfOrder?` {
  case object Allowed extends `AllowOutOfOrder?`
  case object DoNotAllow extends `AllowOutOfOrder?`

  def isOutOfOrder(a: `AllowOutOfOrder?`): Boolean = a match {
    case Allowed    => true
    case DoNotAllow => false
  }
}

sealed trait `IgnoreMissingMigrations?`
object `IgnoreMissingMigrations?` {
  case object Ignore extends `IgnoreMissingMigrations?`
  case object DoNotIgnore extends `IgnoreMissingMigrations?`

  def isIgnoreMissingMigrations(i: `IgnoreMissingMigrations?`): Boolean = i match {
    case Ignore      => true
    case DoNotIgnore => false
  }

  implicit class `IgnoreMissingMigrations?Ops`(val i: `IgnoreMissingMigrations?`) extends AnyVal {
    def isIgnoreMissingMigrations: Boolean = `IgnoreMissingMigrations?`.isIgnoreMissingMigrations(i)
  }
}

sealed trait `IgnoreFutureMigrations?`
object `IgnoreFutureMigrations?` {
  case object Ignore extends `IgnoreFutureMigrations?`
  case object DoNotIgnore extends `IgnoreFutureMigrations?`

  def isIgnoreFutureMigrations(i: `IgnoreFutureMigrations?`): Boolean = i match {
    case Ignore      => true
    case DoNotIgnore => false
  }

  implicit class `IgnoreFutureMigrations?Ops`(val i: `IgnoreFutureMigrations?`) extends AnyVal {
    def isIgnoreFutureMigrations: Boolean = `IgnoreFutureMigrations?`.isIgnoreFutureMigrations(i)
  }
}

sealed trait `ValidateOnMigrate?`
object `ValidateOnMigrate?` {
  case object Validate extends `ValidateOnMigrate?`
  case object DoNotValidate extends `ValidateOnMigrate?`

  def isValidateOnMigrate(v: `ValidateOnMigrate?`): Boolean = v match {
    case Validate      => true
    case DoNotValidate => false
  }

  implicit class `ValidateOnMigrate?Ops`(val v: `ValidateOnMigrate?`) extends AnyVal {
    def isValidateOnMigrate: Boolean = `ValidateOnMigrate?`.isValidateOnMigrate(v)
  }
}

sealed trait `CleanOnValidationError?`
object `CleanOnValidationError?` {
  case object Clean extends `CleanOnValidationError?`
  case object DoNotClean extends `CleanOnValidationError?`

  def isCleanOnValidationError(c: `CleanOnValidationError?`): Boolean = c match {
    case Clean      => true
    case DoNotClean => false
  }

  implicit class `CleanOnValidationError?Ops`(val c: `CleanOnValidationError?`) extends AnyVal {
    def isCleanOnValidationError: Boolean = `CleanOnValidationError?`.isCleanOnValidationError(c)
  }
}

sealed trait `CleanAllowed?`
object `CleanAllowed?` {
  case object CleanAllowed extends `CleanAllowed?`
  case object DoNotAllowClean extends `CleanAllowed?`

  def isCleanDisabled(c: `CleanAllowed?`): Boolean = c match {
    case CleanAllowed    => false
    case DoNotAllowClean => true
  }

  implicit class `CleanAllowed?Ops`(val c: `CleanAllowed?`) extends AnyVal {
    def isCleanDisabled: Boolean = `CleanAllowed?`.isCleanDisabled(c)
  }
}

sealed trait `MixedTransactionAllowed?`
object `MixedTransactionAllowed?` {
  case object AllowMixed extends `MixedTransactionAllowed?`
  case object DoNotAllowMixed extends `MixedTransactionAllowed?`

  def isMixed(m: `MixedTransactionAllowed?`): Boolean = m match {
    case AllowMixed      => true
    case DoNotAllowMixed => false
  }

  implicit class `MixedTransactionAllowed?Ops`(val m: `MixedTransactionAllowed?`) extends AnyVal {
    def isMixed: Boolean = `MixedTransactionAllowed?`.isMixed(m)
  }
}

sealed trait `GroupTransactions?`
object `GroupTransactions?` {
  case object Group extends `GroupTransactions?`
  case object DoNotGroup extends `GroupTransactions?`

  def isGroup(g: `GroupTransactions?`): Boolean = g match {
    case Group      => true
    case DoNotGroup => false
  }

  implicit class `GroupTransactions?Ops`(val g: `GroupTransactions?`) extends AnyVal {
    def isGroup: Boolean = `GroupTransactions?`.isGroup(g)
  }

}

final case class InstalledBy(value: String) extends AnyVal

final case class DryRunOutput(out: OutputStream)

final case class FlutterbyConfig(
    classLoader: ClassLoader,
    dataSource: Option[DataSource],
    baselineVersion: BaselineVersion,
    baselineDescription: BaselineDescription,
    resolvers: MigrationResolvers,
    skipDefaultResolvers: `SkipDefaultResolvers?`,
    skipDefaultCallbacks: `SkipDefaultCallbacks?`,
    sqlMigrationPrefix: SqlMigrationPrefix,
    undoSqlMigrationPrefix: UndoSqlMigrationPrefix,
    repeatableSqlMigrationPrefix: RepeatableSqlMigrationPrefix,
    sqlMigrationSeparator: SqlMigrationSeparator,
    sqlMigrationSuffixes: SqlMigrationSuffixes,
    placeholderReplacement: `ReplacePlaceholders?`,
    placeholderSuffix: PlaceholderSuffix,
    placeholderPrefix: PlaceholderPrefix,
    placeholders: Placeholders,
    target: Option[TargetMigrationVersion],
    table: SchemaHistoryTable,
    schemas: Schemas,
    encoding: SqlMigrationEncoding,
    locations: MigrationLocations,
    baselineOnMigrate: `BaselineOnMigrate?`,
    outOfOrder: `AllowOutOfOrder?`,
    ignoreMissingMigrations: `IgnoreMissingMigrations?`,
    ignoreFutureMigrations: `IgnoreFutureMigrations?`,
    validateOnMigrate: `ValidateOnMigrate?`,
    cleanOnValidationError: `CleanOnValidationError?`,
    clean: `CleanAllowed?`,
    mixed: `MixedTransactionAllowed?`,
    group: `GroupTransactions?`,
    installedBy: Option[InstalledBy],
    dryRunOutput: Option[DryRunOutput]
)

object FlutterbyConfig {
  object Defaults {
    val classLoader: ClassLoader                 = Thread.currentThread.getContextClassLoader
    val dataSource: Option[DataSource]           = None
    val baselineVersion: BaselineVersion         = BaselineVersion(MigrationVersion.fromVersionString(Some("1")))
    val baselineDescription: BaselineDescription = BaselineDescription("<< Flyway Baseline >>")
    val resolvers                                = MigrationResolvers(Vector.empty)
    val skipDefaultResolvers                     = `SkipDefaultResolvers?`.UseDefaultResolvers
    val skipDefaultCallbacks                     = `SkipDefaultCallbacks?`.UseDefaultCallbacks
    val sqlMigrationPrefix                       = SqlMigrationPrefix("V")
    val undoSqlMigrationPrefix                   = UndoSqlMigrationPrefix("U")
    val repeatableSqlMigrationPrefix             = RepeatableSqlMigrationPrefix("R")
    val sqlMigrationSeparator                    = SqlMigrationSeparator("__")
    val sqlMigrationSuffixes                     = SqlMigrationSuffixes(Vector(SqlMigrationSufix(".sql")))
    val placeholderReplacement                   = `ReplacePlaceholders?`.ReplacePlaceholders
    val placeholderSuffix                        = PlaceholderSuffix("}")
    val placeholderPrefix                        = PlaceholderPrefix("${")
    val placeholders                             = Placeholders(Map.empty)
    val target: Option[TargetMigrationVersion]   = None
    val table                                    = SchemaHistoryTable("flyway_schema_history")
    val schemas                                  = Schemas(Vector.empty)
    val encoding                                 = SqlMigrationEncoding(StandardCharsets.UTF_8)
    val locations                                = MigrationLocations(Vector(new Location("db/migration")))
    val baselineOnMigrate                        = `BaselineOnMigrate?`.DoNotBaseline
    val outOfOrder                               = `AllowOutOfOrder?`.DoNotAllow
    val ignoreMissingMigrations                  = `IgnoreMissingMigrations?`.DoNotIgnore
    val ignoreFutureMigrations                   = `IgnoreFutureMigrations?`.Ignore
    val validateOnMigrate                        = `ValidateOnMigrate?`.Validate
    val cleanOnValidationError                   = `CleanOnValidationError?`.DoNotClean
    val clean                                    = `CleanAllowed?`.CleanAllowed
    val mixed                                    = `MixedTransactionAllowed?`.DoNotAllowMixed
    val group                                    = `GroupTransactions?`.DoNotGroup
    val installedBy: Option[InstalledBy]         = None
    val dryRunOutput: Option[DryRunOutput]       = None
  }

  val defaultConfig = FlutterbyConfig(
    classLoader = Defaults.classLoader,
    dataSource = Defaults.dataSource,
    baselineVersion = Defaults.baselineVersion,
    baselineDescription = Defaults.baselineDescription,
    resolvers = Defaults.resolvers,
    skipDefaultResolvers = Defaults.skipDefaultResolvers,
    skipDefaultCallbacks = Defaults.skipDefaultCallbacks,
    sqlMigrationPrefix = Defaults.sqlMigrationPrefix,
    undoSqlMigrationPrefix = Defaults.undoSqlMigrationPrefix,
    repeatableSqlMigrationPrefix = Defaults.repeatableSqlMigrationPrefix,
    sqlMigrationSeparator = Defaults.sqlMigrationSeparator,
    sqlMigrationSuffixes = Defaults.sqlMigrationSuffixes,
    placeholderReplacement = Defaults.placeholderReplacement,
    placeholderSuffix = Defaults.placeholderSuffix,
    placeholderPrefix = Defaults.placeholderPrefix,
    placeholders = Defaults.placeholders,
    target = Defaults.target,
    table = Defaults.table,
    schemas = Defaults.schemas,
    encoding = Defaults.encoding,
    locations = Defaults.locations,
    baselineOnMigrate = Defaults.baselineOnMigrate,
    outOfOrder = Defaults.outOfOrder,
    ignoreMissingMigrations = Defaults.ignoreMissingMigrations,
    ignoreFutureMigrations = Defaults.ignoreFutureMigrations,
    validateOnMigrate = Defaults.validateOnMigrate,
    cleanOnValidationError = Defaults.cleanOnValidationError,
    clean = Defaults.clean,
    mixed = Defaults.mixed,
    group = Defaults.group,
    installedBy = Defaults.installedBy,
    dryRunOutput = Defaults.dryRunOutput
  )

  def toFlyway(c: FlutterbyConfig): FlywayConfiguration = new FlywayConfiguration {
    override def getClassLoader: ClassLoader              = c.classLoader
    override def getDataSource: DataSource                = c.dataSource.orNull
    override def getBaselineVersion: api.MigrationVersion = c.baselineVersion.version.toFlyway
    override def getBaselineDescription: String           = c.baselineDescription.value
    override def getResolvers: Array[api.resolver.MigrationResolver] =
      c.resolvers.resolvers.map(r => FlutterbyMigrationResolver.toFlyway(r, c)).toArray
    override def isSkipDefaultResolvers: Boolean            = c.skipDefaultResolvers.isSkipDefaultResolvers
    override def getCallbacks: Array[api.callback.Callback] = Array.empty //TODO: address
    override def isSkipDefaultCallbacks: Boolean            = c.skipDefaultCallbacks.isSkipDefaultCallbacks
    override def getSqlMigrationPrefix: String              = c.sqlMigrationPrefix.value
    override def getUndoSqlMigrationPrefix: String          = c.undoSqlMigrationPrefix.value
    override def getRepeatableSqlMigrationPrefix: String    = c.repeatableSqlMigrationPrefix.value
    override def getSqlMigrationSeparator: String           = c.sqlMigrationSeparator.value
    override def getSqlMigrationSuffixes: Array[String] =
      c.sqlMigrationSuffixes.sqlMigrationSuffixes.map(_.value).toArray
    override def isPlaceholderReplacement: Boolean = c.placeholderReplacement.isPlaceholderReplacement
    override def getPlaceholderSuffix: String      = c.placeholderSuffix.value
    override def getPlaceholderPrefix: String      = c.placeholderPrefix.value
    override def getPlaceholders: util.Map[String, String] =
      CollectionConversions.toJavaMap(c.placeholders.placeholders)
    override def getTarget: api.MigrationVersion                             = c.target.map(t => MigrationVersion.toFlyway(t.target)).orNull
    override def getTable: String                                            = c.table.value
    override def getSchemas: Array[String]                                   = c.schemas.schemas.toArray
    override def getEncoding: Charset                                        = c.encoding.value
    override def getLocations: Array[Location]                               = c.locations.locations.toArray //TODO: address
    override def isBaselineOnMigrate: Boolean                                = c.baselineOnMigrate.isBaselineOnMigrate
    override def isOutOfOrder: Boolean                                       = `AllowOutOfOrder?`.isOutOfOrder(c.outOfOrder)
    override def isIgnoreMissingMigrations: Boolean                          = c.ignoreMissingMigrations.isIgnoreMissingMigrations
    override def isIgnoreFutureMigrations: Boolean                           = c.ignoreFutureMigrations.isIgnoreFutureMigrations
    override def isValidateOnMigrate: Boolean                                = c.validateOnMigrate.isValidateOnMigrate
    override def isCleanOnValidationError: Boolean                           = c.cleanOnValidationError.isCleanOnValidationError
    override def isCleanDisabled: Boolean                                    = c.clean.isCleanDisabled
    override def isMixed: Boolean                                            = c.mixed.isMixed
    override def isGroup: Boolean                                            = c.group.isGroup
    override def getInstalledBy: String                                      = c.installedBy.map(_.value).orNull
    @silent("deprecated") override def getErrorHandlers: Array[ErrorHandler] = Array.empty
    override def getDryRunOutput: OutputStream                               = c.dryRunOutput.map(_.out).orNull

    override def getConnectRetries: Int             = ??? //TODO: implement
    override def getInitSql: String                 = ???
    override def isIgnoreIgnoredMigrations: Boolean = ???
    override def isIgnorePendingMigrations: Boolean = ???
    override def getErrorOverrides: Array[String]   = ???
    override def isStream: Boolean                  = ???
    override def isBatch: Boolean                   = ???
    override def isOracleSqlplus: Boolean           = ???
    override def getLicenseKey: String              = ???
  }

  def fromFlyway(c: FlywayConfiguration): FlutterbyConfig = {
    val _ = c
    ???
  } //TODO: Implement me

  implicit class FlutterbyConfigOps(val c: FlutterbyConfig) extends AnyVal {
    def toFlyway: FlywayConfiguration = FlutterbyConfig.toFlyway(c)
  }
}
