package flutterby.cats.config

import java.nio.charset.StandardCharsets
import java.util

import flutterby.core.jdk.CollectionConversions
import org.flywaydb.core.api.{executor, resolver, MigrationType, MigrationVersion}
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.api.executor.MigrationExecutor
import org.flywaydb.core.api.migration.{Context, JavaMigration}
import org.flywaydb.core.api.resolver.{MigrationResolver, ResolvedMigration}
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.util.{Failure, Success, Try}

object Generators  {
  val genMigrationVersionString = Gen
    .oneOf[String](Gen.const(null), Gen.const("current"), Gen.const("latest"), Gen.posNum[Int].map(_.toString))

  val genJdbcUrl = for {
    dbName <- Gen.alphaNumStr.suchThat(v => Option(v).exists(_.trim.length > 0))
  } yield s"jdbc:postgresql://127.0.0.1:5432/$dbName"
}

object Arbitraries {

  implicit val arbMigrationVersion: Arbitrary[MigrationVersion] = Arbitrary(
    Generators.genMigrationVersionString
      .map(MigrationVersion.fromVersion)
  )

  implicit val arbJavaMigration: Arbitrary[JavaMigration] = Arbitrary {
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

  implicit val arbMigrationType: Arbitrary[MigrationType] = Arbitrary(Gen.oneOf(MigrationType.values()))

  implicit val arbMigrationExecutor: Arbitrary[MigrationExecutor]     = Arbitrary {
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

  implicit val arbResolvedMigration: Arbitrary[ResolvedMigration]     = Arbitrary {
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

  implicit val arbMigrationResolver: Arbitrary[MigrationResolver]     = Arbitrary {
    for {
      resolvedMigrations <- Gen.listOf(Arbitrary.arbitrary[ResolvedMigration])
    } yield new MigrationResolver {
      override def resolveMigrations(context: resolver.Context): util.Collection[ResolvedMigration] =
        CollectionConversions.toJavaCollection(resolvedMigrations)
    }
  }

  implicit val arbFluentConfiguration: Arbitrary[FluentConfiguration] =
    Arbitrary {
      for {
        locationPrefix               <- Gen.oneOf("filesystem:", "classpath:")
        location                     <- Gen.alphaNumStr
        fullLocation                  = locationPrefix + location
        encoding                     <- Gen.oneOf(StandardCharsets.UTF_8, StandardCharsets.US_ASCII, StandardCharsets.ISO_8859_1)
        defaultSchema                <- Gen.alphaNumStr
        schemas                      <- Gen.listOf(Gen.alphaNumStr)
        table                        <- Gen.alphaNumStr
        tableSpace                   <- Gen.alphaNumStr
        target                       <- Generators.genMigrationVersionString
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
        baselineVersion              <- Generators.genMigrationVersionString
        baselineDescription          <- Gen.asciiPrintableStr
        baselineOnMigrate            <- Arbitrary.arbitrary[Boolean]
        outOfOrder                   <- Arbitrary.arbitrary[Boolean]
        resolvers                    <- Gen.listOf(Arbitrary.arbitrary[MigrationResolver])
        skipDefaultResolvers         <- Arbitrary.arbitrary[Boolean]
        jdbcUrl                      <- Generators.genJdbcUrl
        username                     <- Gen.asciiPrintableStr
        password                     <- Gen.asciiPrintableStr
        connectRetries               <- Gen.chooseNum[Int](0, Int.MaxValue)
        initSql                      <- Gen.asciiPrintableStr
        mixed                        <- Arbitrary.arbitrary[Boolean]
        installedBy                  <- Gen.asciiPrintableStr
        group                        <- Arbitrary.arbitrary[Boolean]
        licenseKey                   <- Gen.asciiPrintableStr
      } yield new FluentConfiguration(Thread.currentThread.getContextClassLoader) // TODO: handle other variations
        .locations(fullLocation)
        .encoding(encoding)
        .defaultSchema(defaultSchema)
        .schemas(schemas: _*)
        .table(table)
        .tablespace(tableSpace)                                                   // TODO: is this in the config?
        .target(target)
        .placeholderReplacement(isPlaceholderReplacement)
        .placeholders(CollectionConversions.toJavaMap(placeholders))
        .placeholderPrefix(placeholderPrefix)
        .placeholderSuffix(placeholderSuffix)
        .sqlMigrationPrefix(sqlMigrationPrefix)
        .repeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix)
        .sqlMigrationSeparator(sqlMigrationSeparator)
        .sqlMigrationSuffixes(sqlMigrationSuffixes: _*)
        .javaMigrations(javaMigrations: _*)                                       // TODO: is this in the config?
        .ignoreMissingMigrations(ignoreMissingMigrations)
        .ignoreIgnoredMigrations(ignoreIgnoredMigrations)
        .ignorePendingMigrations(ignorePendingMigrations)
        .ignoreFutureMigrations(ignoreFutureMigrations)
        .validateMigrationNaming(validateMigrationNaming)
        .validateOnMigrate(validateOnMigrate)
        .cleanOnValidationError(cleanOnValidationError)
        .cleanDisabled(cleanDisabled)
        .baselineVersion(baselineVersion)
        .baselineDescription(baselineDescription)
        .baselineOnMigrate(baselineOnMigrate)
        .outOfOrder(outOfOrder)
        .resolvers(resolvers: _*)
        .skipDefaultResolvers(skipDefaultResolvers)
        .dataSource(jdbcUrl, username, password)
        .connectRetries(connectRetries)
        .initSql(initSql)
        .mixed(mixed)
        .installedBy(installedBy)
        .group(group)
        .licenseKey(licenseKey)
    }
}

class ConfigBuilderSpec extends Specification with ScalaCheck {
  import Arbitraries._
  "must have be the same" in prop { (f: FluentConfiguration) =>
    val _ = f
    ko
  }
}
