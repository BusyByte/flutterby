package flutterby.cats.config

import java.io.PrintWriter
import java.nio.charset.{Charset, StandardCharsets}
import java.sql.Connection
import java.util
import java.util.logging.Logger

import cats.effect.IO
import flutterby.cats.config.TestData.{
  Callback1,
  Callback2,
  Callback3,
  JdbcUrl,
  JdbcUrlDatasource,
  MigrationResolver1,
  MigrationResolver2,
  MigrationResolver3,
  Password,
  StringCallback,
  StringEncoding,
  StringLocation,
  StringMigrationResolver,
  StringVersion,
  TestDataSource,
  TestDataSourceImpl,
  Username
}
import flutterby.core.jdk.CollectionConversions
import javax.sql.DataSource
import org.flywaydb.core.api.{callback, executor, resolver, Location, MigrationType, MigrationVersion}
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.executor.MigrationExecutor
import org.flywaydb.core.api.migration.{Context, JavaMigration}
import org.flywaydb.core.api.resolver.{MigrationResolver, ResolvedMigration}
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import org.flywaydb.core.api.callback.{Callback, Event}

import scala.util.{Failure, Success, Try}

object TestData    {
  final case class StringLocation(value: String) extends AnyVal

  final case class StringEncoding(value: String) extends AnyVal

  final case class StringVersion(value: String) extends AnyVal

  final case class StringCallback(value: String) extends AnyVal

  final class Callback1                                   extends Callback {
    override def supports(event: Event, context: callback.Context): Boolean               = false
    override def canHandleInTransaction(event: Event, context: callback.Context): Boolean = false
    override def handle(event: Event, context: callback.Context): Unit                    = ()
  }
  final class Callback2                                   extends Callback {
    override def supports(event: Event, context: callback.Context): Boolean               = false
    override def canHandleInTransaction(event: Event, context: callback.Context): Boolean = false
    override def handle(event: Event, context: callback.Context): Unit                    = ()
  }
  final class Callback3                                   extends Callback {
    override def supports(event: Event, context: callback.Context): Boolean               = false
    override def canHandleInTransaction(event: Event, context: callback.Context): Boolean = false
    override def handle(event: Event, context: callback.Context): Unit                    = ()
  }

  final case class StringMigrationResolver(value: String) extends AnyVal

  final class MigrationResolver1           extends MigrationResolver {
    override def resolveMigrations(context: resolver.Context): util.Collection[ResolvedMigration] =
      CollectionConversions.toJavaCollection(List.empty)
  }
  final class MigrationResolver2           extends MigrationResolver {
    override def resolveMigrations(context: resolver.Context): util.Collection[ResolvedMigration] =
      CollectionConversions.toJavaCollection(List.empty)
  }
  final class MigrationResolver3           extends MigrationResolver {
    override def resolveMigrations(context: resolver.Context): util.Collection[ResolvedMigration] =
      CollectionConversions.toJavaCollection(List.empty)
  }

  final case class JdbcUrl(value: String)  extends AnyVal
  final case class Username(value: String) extends AnyVal
  final case class Password(value: String) extends AnyVal

  sealed trait TestDataSource
  final case class JdbcUrlDatasource(url: JdbcUrl, username: Username, password: Password) extends TestDataSource

  final object TestDataSourceImpl extends DataSource with TestDataSource {
    val jdcbUrl                                                                = "TestDataSourceImplUrl"
    val username                                                               = "TestUsername"
    val password                                                               = "TestPassword"
    override def getConnection: Connection                                     = null
    override def getConnection(username: String, password: String): Connection = null
    override def unwrap[T](iface: Class[T]): T                                 = iface.getConstructor().newInstance()
    override def isWrapperFor(iface: Class[_]): Boolean                        = false
    override def getLogWriter: PrintWriter                                     = new PrintWriter(System.out)
    override def setLogWriter(out: PrintWriter): Unit                          = ()
    override def setLoginTimeout(seconds: Int): Unit                           = ()
    override def getLoginTimeout: Int                                          = 2
    override def getParentLogger: Logger                                       = Logger.getGlobal
  }
}

object Arbitraries {

  implicit val arbJdbcUrl: Arbitrary[JdbcUrl]   = Arbitrary {
    for {
      dbName <- Gen.alphaNumStr.suchThat(v => Option(v).exists(_.trim.length > 0))
    } yield JdbcUrl(s"jdbc:postgresql://127.0.0.1:5432/$dbName")
  }

  implicit val arbUsername: Arbitrary[Username] =
    Arbitrary(Gen.asciiPrintableStr.map(Username.apply))

  implicit val arbPassword: Arbitrary[Password] =
    Arbitrary(Gen.asciiPrintableStr.map(Password.apply))

  implicit val arbJdbcUrlDatasource: Arbitrary[JdbcUrlDatasource]             = Arbitrary {
    for {
      jdbcUrl  <- Arbitrary.arbitrary[JdbcUrl]
      username <- Arbitrary.arbitrary[Username]
      password <- Arbitrary.arbitrary[Password]
    } yield JdbcUrlDatasource(jdbcUrl, username, password)
  }

  implicit val arbStringMigrationResolver: Arbitrary[StringMigrationResolver] = Arbitrary {
    for {
      migrator <-
        Gen.oneOf(classOf[MigrationResolver1], classOf[MigrationResolver2], classOf[MigrationResolver3]).map(_.getName)
    } yield StringMigrationResolver(migrator)
  }

  implicit val arbStringCallback: Arbitrary[StringCallback]                   = Arbitrary {
    for {
      callback <- Gen.oneOf(classOf[Callback1], classOf[Callback2], classOf[Callback3]).map(_.getName)
    } yield StringCallback(callback)
  }

  implicit val arbCallback: Arbitrary[Callback]                               = Arbitrary {
    for {
      supportsResult               <- Arbitrary.arbitrary[Boolean]
      canHandleInTransactionResult <- Arbitrary.arbitrary[Boolean]
    } yield new Callback {
      override def supports(event: Event, context: callback.Context): Boolean               = supportsResult
      override def canHandleInTransaction(event: Event, context: callback.Context): Boolean =
        canHandleInTransactionResult
      override def handle(event: Event, context: callback.Context): Unit                    = ()
    }
  }

  implicit val arbStringVersion: Arbitrary[StringVersion]                     = Arbitrary {
    for {
      target <-
        Gen.oneOf[String](Gen.const(null), Gen.const("current"), Gen.const("latest"), Gen.posNum[Int].map(_.toString))
    } yield StringVersion(target)
  }

  implicit val arbMigrationVersion: Arbitrary[MigrationVersion]               = Arbitrary {
    for {
      version <- Arbitrary.arbitrary[StringVersion]
    } yield MigrationVersion.fromVersion(version.value)
  }

  implicit val arbMigrationType: Arbitrary[MigrationType]                     = Arbitrary(Gen.oneOf(MigrationType.values().toList))

  implicit val arbMigrationExecutor: Arbitrary[MigrationExecutor] = Arbitrary {
    for {
      executeResult        <- Gen
                         .oneOf[Try[Unit]](Failure(new RuntimeException("Boom!")), Success(()))
      executeInTransaction <- Arbitrary.arbitrary[Boolean]
    } yield new MigrationExecutor {
      override def execute(context: executor.Context): Unit =
        executeResult.get
      override def canExecuteInTransaction: Boolean         = executeInTransaction
    }
  }

  implicit val arbResolvedMigration: Arbitrary[ResolvedMigration] = Arbitrary {
    for {
      version                                    <- Arbitrary.arbitrary[MigrationVersion]
      description                                <- Gen.asciiPrintableStr
      script                                     <- Gen.alphaNumStr
      checksum                                   <- Gen.posNum[Int]
      migrationType                              <- Arbitrary.arbitrary[MigrationType]
      physicalLocation                           <- Gen.alphaNumStr
      executor                                   <- Arbitrary.arbitrary[MigrationExecutor]
      checksumMatchesResult                      <- Arbitrary.arbitrary[Boolean]
      checksumMatchesWithoutBeingIdenticalResult <- Arbitrary.arbitrary[Boolean]
    } yield new ResolvedMigration {
      override def getVersion: MigrationVersion                                     = version
      override def getDescription: String                                           = description
      override def getScript: String                                                = script
      override def getChecksum: Integer                                             = checksum
      override def getType: MigrationType                                           = migrationType
      override def getPhysicalLocation: String                                      = physicalLocation
      override def getExecutor: MigrationExecutor                                   = executor
      override def checksumMatches(checksum: Integer): Boolean                      =
        checksumMatchesResult
      override def checksumMatchesWithoutBeingIdentical(checksum: Integer): Boolean =
        checksumMatchesWithoutBeingIdenticalResult
    }
  }

  implicit val arbMigrationResolver: Arbitrary[MigrationResolver] = Arbitrary {
    for {
      resolvedMigrations <- Gen.listOf(Arbitrary.arbitrary[ResolvedMigration])
    } yield new MigrationResolver {
      override def resolveMigrations(context: resolver.Context): util.Collection[ResolvedMigration] =
        CollectionConversions.toJavaCollection(resolvedMigrations)
    }
  }

  implicit val arbStringLocation: Arbitrary[StringLocation]       = Arbitrary {
    for {
      locationPrefix <- Gen.oneOf("filesystem:", "classpath:")
      location       <- Gen.alphaNumStr
    } yield StringLocation(locationPrefix + location)
  }

  implicit val arbLocation: Arbitrary[Location]                   = Arbitrary {
    for {
      loc <- Arbitrary.arbitrary[StringLocation]
    } yield new Location(loc.value)
  }

  implicit val arbCharsetEncoding: Arbitrary[Charset]             = Arbitrary {
    for {
      encoding <- Gen.oneOf(StandardCharsets.UTF_8, StandardCharsets.US_ASCII, StandardCharsets.ISO_8859_1)
    } yield encoding
  }

  implicit val arbStringEncoding: Arbitrary[StringEncoding]       = Arbitrary {
    for {
      encoding <- Arbitrary.arbitrary[Charset].map(_.name())
    } yield StringEncoding(encoding)
  }

  implicit val arbJavaMigration: Arbitrary[JavaMigration]         = Arbitrary {
    for {
      version              <- Arbitrary.arbitrary[MigrationVersion]
      description          <- Gen.asciiPrintableStr
      checksum             <- Gen.posNum[Int]
      undo                 <- Arbitrary.arbitrary[Boolean]
      executeInTransaction <- Arbitrary.arbitrary[Boolean]
      migrateResult        <- Gen
                         .oneOf[Try[Unit]](Failure(new RuntimeException("Boom!")), Success(()))
    } yield new JavaMigration {
      override def getVersion: MigrationVersion     = version
      override def getDescription: String           = description
      override def getChecksum: Integer             = checksum
      override def isUndo: Boolean                  = undo
      override def canExecuteInTransaction: Boolean = executeInTransaction
      override def migrate(context: Context): Unit  = migrateResult.get
    }
  }

  type FluentEndo = FluentConfiguration => FluentConfiguration

  final case class FluentConfigurationWithDatasource(fluentConfiguration: FluentConfiguration, ds: TestDataSource)

  implicit val arbFluentConfiguration: Arbitrary[FluentConfigurationWithDatasource] =
    Arbitrary {
      for {
        locationsEndo                <- Gen.oneOf(
                           Gen
                             .listOf(Arbitrary.arbitrary[StringLocation])
                             .map[FluentEndo](l => c => c.locations(l.map(_.value): _*)),
                           Gen.listOf(Arbitrary.arbitrary[Location]).map[FluentEndo](l => c => c.locations(l: _*))
                         )
        encodingEndo                 <- Gen.oneOf(
                          Arbitrary.arbitrary[StringEncoding].map[FluentEndo](e => c => c.encoding(e.value)),
                          Arbitrary.arbitrary[Charset].map[FluentEndo](e => c => c.encoding(e))
                        )
        targetEndo                   <- Gen.oneOf(
                        Arbitrary.arbitrary[StringVersion].map[FluentEndo](e => c => c.target(e.value)),
                        Arbitrary.arbitrary[MigrationVersion].map[FluentEndo](e => c => c.target(e))
                      )
        baselineVersionEndo          <- Gen.oneOf(
                                 Arbitrary.arbitrary[StringVersion].map[FluentEndo](e => c => c.target(e.value)),
                                 Arbitrary.arbitrary[MigrationVersion].map[FluentEndo](e => c => c.target(e))
                               )
        callbacksEndo                <- Gen.oneOf(
                           Gen
                             .listOf(Arbitrary.arbitrary[StringCallback])
                             .map[FluentEndo](l => c => c.callbacks(l.map(_.value): _*)),
                           Gen.listOf(Arbitrary.arbitrary[Callback]).map[FluentEndo](l => c => c.callbacks(l: _*))
                         )
        resolversEndo                <- Gen.oneOf(
                           Gen
                             .listOf(Arbitrary.arbitrary[StringMigrationResolver])
                             .map[FluentEndo](l => c => c.resolvers(l.map(_.value): _*)),
                           Gen
                             .listOf(Arbitrary.arbitrary[MigrationResolver])
                             .map[FluentEndo](l => c => c.resolvers(l: _*))
                         )
        dataSource                   <- Gen.oneOf[TestDataSource](
                        Arbitrary.arbitrary[JdbcUrlDatasource],
                        Gen.const(TestDataSourceImpl)
                      )
        dataSourceEndo                = dataSource match {
                           case JdbcUrlDatasource(url, username, password) =>
                             (c: FluentConfiguration) => c.dataSource(url.value, username.value, password.value)
                           case t: TestDataSourceImpl.type                 => (c: FluentConfiguration) => c.dataSource(t)
                         }
        defaultSchema                <- Gen.alphaNumStr
        schemas                      <- Gen.listOf(Gen.alphaNumStr)
        table                        <- Gen.alphaNumStr
        tableSpace                   <- Gen.alphaNumStr
        isPlaceholderReplacement     <- Arbitrary.arbitrary[Boolean]
        placeholders                 <- Gen.mapOf(Gen.alphaNumStr.flatMap(a => Gen.alphaNumStr.map(b => (a, b))))
        placeholderPrefix            <- Gen.alphaNumStr.suchThat(v => Option(v).exists(_.trim.length > 0))
        placeholderSuffix            <- Gen.alphaNumStr.suchThat(v => Option(v).exists(_.trim.length > 0))
        sqlMigrationPrefix           <- Gen.alphaNumStr
        repeatableSqlMigrationPrefix <- Gen.alphaNumStr
        sqlMigrationSeparator        <- Gen.alphaNumStr.suchThat(v => Option(v).exists(_.trim.length > 0))
        sqlMigrationSuffixes         <- Gen.listOf(Gen.alphaNumStr)
        javaMigrations               <- Gen.listOf(Arbitrary.arbitrary[JavaMigration])
        ignoreMissingMigrations      <- Arbitrary.arbitrary[Boolean]
        ignoreIgnoredMigrations      <- Arbitrary.arbitrary[Boolean]
        ignorePendingMigrations      <- Arbitrary.arbitrary[Boolean]
        ignoreFutureMigrations       <- Arbitrary.arbitrary[Boolean]
        validateMigrationNaming      <- Arbitrary.arbitrary[Boolean]
        validateOnMigrate            <- Arbitrary.arbitrary[Boolean]
        cleanOnValidationError       <- Arbitrary.arbitrary[Boolean]
        cleanDisabled                <- Arbitrary.arbitrary[Boolean]
        baselineDescription          <- Gen.asciiPrintableStr
        baselineOnMigrate            <- Arbitrary.arbitrary[Boolean]
        skipDefaultCallbacks         <- Arbitrary.arbitrary[Boolean]
        outOfOrder                   <- Arbitrary.arbitrary[Boolean]
        skipDefaultResolvers         <- Arbitrary.arbitrary[Boolean]
        connectRetries               <- Gen.chooseNum[Int](0, Int.MaxValue)
        initSql                      <- Gen.asciiPrintableStr
        mixed                        <- Arbitrary.arbitrary[Boolean]
        installedBy                  <- Gen.asciiPrintableStr
        group                        <- Arbitrary.arbitrary[Boolean]
        licenseKey                   <- Gen.asciiPrintableStr
        f1                            = new FluentConfiguration(Thread.currentThread.getContextClassLoader)
               .defaultSchema(defaultSchema)
               .schemas(schemas: _*)
               .table(table)
               .tablespace(tableSpace)
               .placeholderReplacement(isPlaceholderReplacement)
               .placeholders(CollectionConversions.toJavaMap(placeholders))
               .placeholderPrefix(placeholderPrefix)
               .placeholderSuffix(placeholderSuffix)
               .sqlMigrationPrefix(sqlMigrationPrefix)
               .repeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix)
               .sqlMigrationSeparator(sqlMigrationSeparator)
               .sqlMigrationSuffixes(sqlMigrationSuffixes: _*)
               .javaMigrations(javaMigrations: _*)
               .ignoreMissingMigrations(ignoreMissingMigrations)
               .ignoreIgnoredMigrations(ignoreIgnoredMigrations)
               .ignorePendingMigrations(ignorePendingMigrations)
               .ignoreFutureMigrations(ignoreFutureMigrations)
               .validateMigrationNaming(validateMigrationNaming)
               .validateOnMigrate(validateOnMigrate)
               .cleanOnValidationError(cleanOnValidationError)
               .cleanDisabled(cleanDisabled)
               .baselineDescription(baselineDescription)
               .baselineOnMigrate(baselineOnMigrate)
               .skipDefaultCallbacks(skipDefaultCallbacks)
               .outOfOrder(outOfOrder)
               .skipDefaultResolvers(skipDefaultResolvers)
               .connectRetries(connectRetries)
               .initSql(initSql)
               .mixed(mixed)
               .installedBy(installedBy)
               .group(group)
               .licenseKey(licenseKey)
        f2                            = locationsEndo(f1)
        f3                            = encodingEndo(f2)
        f4                            = targetEndo(f3)
        f5                            = baselineVersionEndo(f4)
        f6                            = callbacksEndo(f5)
        f7                            = resolversEndo(f6)
        f8                            = dataSourceEndo(f7)
      } yield FluentConfigurationWithDatasource(f8, dataSource)
    }
}

class ConfigBuilderSpec extends Specification with ScalaCheck {
  import Arbitraries._
  "must have be the same" in prop { (f: FluentConfigurationWithDatasource) =>
    val fluentConfig = f.fluentConfiguration
    import _root_.flutterby.cats.syntax.all._

    val dsOp: ConfigBuilder[IO] => ConfigBuilder[IO] = f.ds match {
      case JdbcUrlDatasource(url, username, password) =>
        (c: ConfigBuilder[IO]) => c.dataSource(url.value, username.value, password.value)
      case t: TestDataSourceImpl.type                 => (c: ConfigBuilder[IO]) => c.dataSource(t)
    }

    val cb: ConfigBuilder[IO]                        = ConfigBuilder
      .impl[IO]
      .group(fluentConfig.isGroup)
      .installedBy(fluentConfig.getInstalledBy)
      .mixed(fluentConfig.isMixed)
      .ignoreMissingMigrations(fluentConfig.isIgnoreMissingMigrations)
      .ignoreIgnoredMigrations(fluentConfig.isIgnoreIgnoredMigrations)
      .ignorePendingMigrations(fluentConfig.isIgnorePendingMigrations)
      .ignoreFutureMigrations(fluentConfig.isIgnoreFutureMigrations)
      .validateMigrationNaming(fluentConfig.isValidateMigrationNaming)
      .validateOnMigrate(fluentConfig.isValidateOnMigrate)
      .cleanOnValidationError(fluentConfig.isCleanOnValidationError)
      .cleanDisabled(fluentConfig.isCleanDisabled)
      .locations(fluentConfig.getLocations.toList)
      .encoding(fluentConfig.getEncoding)
      .defaultSchema(fluentConfig.getDefaultSchema)
      .schemas(fluentConfig.getSchemas.toList: _*)
      .table(fluentConfig.getTable)
      .tablespace(fluentConfig.getTablespace)
      .target(fluentConfig.getTarget)
      .placeholderReplacement(fluentConfig.isPlaceholderReplacement)
      .placeholders(CollectionConversions.toScalaMap(fluentConfig.getPlaceholders))
      .placeholderPrefix(fluentConfig.getPlaceholderPrefix)
      .placeholderSuffix(fluentConfig.getPlaceholderSuffix)
      .sqlMigrationPrefix(fluentConfig.getSqlMigrationPrefix)
      .repeatableSqlMigrationPrefix(fluentConfig.getRepeatableSqlMigrationPrefix)
      .sqlMigrationSeparator(fluentConfig.getSqlMigrationSeparator)
      .sqlMigrationSuffixes(fluentConfig.getSqlMigrationSuffixes.toList: _*)
      .javaMigrations(fluentConfig.getJavaMigrations.toList: _*)
      .connectRetries(fluentConfig.getConnectRetries)
      .initSql(fluentConfig.getInitSql)
      .baselineVersion(fluentConfig.getBaselineVersion)
      .baselineDescription(fluentConfig.getBaselineDescription)
      .baselineOnMigrate(fluentConfig.isBaselineOnMigrate)
      .outOfOrder(fluentConfig.isOutOfOrder)
      .callbacks(fluentConfig.getCallbacks.toList: _*)
      .skipDefaultCallbacks(fluentConfig.isSkipDefaultCallbacks)
      .resolvers(fluentConfig.getResolvers.toList: _*)
      .skipDefaultResolvers(fluentConfig.isSkipDefaultResolvers)

    val configBuilder = dsOp(cb)

    val resultingConfig = configBuilder.build(fluentConfig.getClassLoader).config.unsafeRunSync()

    resultingConfig.getClassLoader must_== fluentConfig.getClassLoader

    val finalDataSource  = extractDataSourceData(resultingConfig.getDataSource).get
    val fluentDataSource = extractDataSourceData(fluentConfig.getDataSource).get
    finalDataSource must_== fluentDataSource

    resultingConfig.isGroup must_== fluentConfig.isGroup
    resultingConfig.getInstalledBy must_== fluentConfig.getInstalledBy
    resultingConfig.isMixed must_== fluentConfig.isMixed
    resultingConfig.isIgnoreMissingMigrations must_== fluentConfig.isIgnoreMissingMigrations
    resultingConfig.isIgnoreIgnoredMigrations must_== fluentConfig.isIgnoreIgnoredMigrations
    resultingConfig.isIgnorePendingMigrations must_== fluentConfig.isIgnorePendingMigrations
    resultingConfig.isIgnoreFutureMigrations must_== fluentConfig.isIgnoreFutureMigrations
    resultingConfig.isValidateMigrationNaming must_== fluentConfig.isValidateMigrationNaming
    resultingConfig.isValidateOnMigrate must_== fluentConfig.isValidateOnMigrate
    resultingConfig.isCleanOnValidationError must_== fluentConfig.isCleanOnValidationError
    resultingConfig.isCleanDisabled must_== fluentConfig.isCleanDisabled
    resultingConfig.getLocations must_== fluentConfig.getLocations
    resultingConfig.getEncoding must_== fluentConfig.getEncoding
    resultingConfig.getDefaultSchema must_== fluentConfig.getDefaultSchema
    resultingConfig.getSchemas must_== fluentConfig.getSchemas
    resultingConfig.getTable must_== fluentConfig.getTable
    resultingConfig.getTarget must_== fluentConfig.getTarget
    resultingConfig.isPlaceholderReplacement must_== fluentConfig.isPlaceholderReplacement
    resultingConfig.getPlaceholders must_== fluentConfig.getPlaceholders
    resultingConfig.getPlaceholderPrefix must_== fluentConfig.getPlaceholderPrefix
    resultingConfig.getPlaceholderSuffix must_== fluentConfig.getPlaceholderSuffix
    resultingConfig.getSqlMigrationPrefix must_== fluentConfig.getSqlMigrationPrefix
    resultingConfig.getRepeatableSqlMigrationPrefix must_== fluentConfig.getRepeatableSqlMigrationPrefix
    resultingConfig.getSqlMigrationSeparator must_== fluentConfig.getSqlMigrationSeparator
    resultingConfig.getSqlMigrationSuffixes must_== fluentConfig.getSqlMigrationSuffixes
    resultingConfig.getJavaMigrations must_== fluentConfig.getJavaMigrations
    resultingConfig.getConnectRetries must_== fluentConfig.getConnectRetries
    resultingConfig.getInitSql must_== fluentConfig.getInitSql
    resultingConfig.getBaselineVersion must_== fluentConfig.getBaselineVersion
    resultingConfig.getBaselineDescription must_== fluentConfig.getBaselineDescription
    resultingConfig.isBaselineOnMigrate must_== fluentConfig.isBaselineOnMigrate
    resultingConfig.isOutOfOrder must_== fluentConfig.isOutOfOrder
    resultingConfig.getCallbacks must_== fluentConfig.getCallbacks
    resultingConfig.isSkipDefaultCallbacks must_== fluentConfig.isSkipDefaultCallbacks
    resultingConfig.getResolvers must_== fluentConfig.getResolvers
    resultingConfig.isSkipDefaultResolvers must_== fluentConfig.isSkipDefaultResolvers
    resultingConfig.getTablespace must_== fluentConfig.getTablespace
  }

  def extractDataSourceData(ds: DataSource): Option[(String, String, String)] =
    ds match {
      case dds: DriverDataSource      => Some((dds.getUrl, dds.getUser, dds.getPassword))
      case t: TestDataSourceImpl.type => Some((t.jdcbUrl, t.username, t.password))
      case _                          => None
    }
}
