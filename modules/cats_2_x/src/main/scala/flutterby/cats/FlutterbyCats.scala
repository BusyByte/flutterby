package flutterby.cats

import cats.effect.Sync
import flutterby.core.{AllMigrationInfo, Flutterby, MigrationInfo}
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.{MigrationInfoService => FlywayMigrationInfoService}
import cats.implicits._
import flutterby.cats.config.ConfigBuilder
import org.flywaydb.core.api.configuration.Configuration

package config {

  import java.io.{File, OutputStream}
  import java.nio.charset.Charset
  import java.util.Properties

  import javax.sql.DataSource
  import org.flywaydb.core.api.callback.Callback
  import org.flywaydb.core.api.{Location, MigrationVersion}
  import org.flywaydb.core.api.configuration.FluentConfiguration
  import org.flywaydb.core.api.resolver.MigrationResolver

  final class ConfigBuilder[F[_]: Sync] private[config](private val f: F[FluentConfiguration])
  object ConfigBuilder {
    def impl[F[_]](implicit F: Sync[F]): ConfigBuilder[F] = new ConfigBuilder[F](F.delay(new FluentConfiguration()))

    implicit class ConfigOps[F[_]](val c: ConfigBuilder[F])(implicit F: Sync[F]) {

      def dataSource(dataSource: DataSource): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.dataSource(dataSource))
        } yield updated
      )

      def dataSource(url: String, user: String, password: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
            config <- c.f
            updated <- F.delay(config.dataSource(url, user, password))
        } yield updated
      )

      def dryRunOutput(dryRunOutput: OutputStream): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.dryRunOutput(dryRunOutput))
        } yield updated
      )
      def dryRunOutput(dryRunOutput: File): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.dryRunOutput(dryRunOutput))
        } yield updated
      )
      def dryRunOutput(dryRunOutputFileName: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.dryRunOutput(dryRunOutputFileName))
        } yield updated
      )
      def errorOverrides(errorOverrides: String*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.errorOverrides(errorOverrides: _*))
        } yield updated
      )
      def group(group: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.group(group))
        } yield updated
      )
      def installedBy(installedBy: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.installedBy(installedBy))
        } yield updated
      )
      def mixed(mixed: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.mixed(mixed))
        } yield updated
      )
      def ignoreMissingMigrations(ignoreMissingMigrations: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.ignoreMissingMigrations(ignoreMissingMigrations))
        } yield updated
      )
      def ignoreIgnoredMigrations(ignoreIgnoredMigrations: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.ignoreIgnoredMigrations(ignoreIgnoredMigrations))
        } yield updated
      )
      def ignorePendingMigrations(ignorePendingMigrations: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.ignorePendingMigrations(ignorePendingMigrations))
        } yield updated
      )
      def ignoreFutureMigrations(ignoreFutureMigrations: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.ignoreFutureMigrations(ignoreFutureMigrations))
        } yield updated
      )
      def validateOnMigrate(validateOnMigrate: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.validateOnMigrate(validateOnMigrate))
        } yield updated
      )
      def cleanOnValidationError(cleanOnValidationError: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.cleanOnValidationError(cleanOnValidationError))
        } yield updated
      )
      def cleanDisabled(cleanDisabled: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.cleanDisabled(cleanDisabled))
        } yield updated
      )
      def locations(locations: String*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.locations(locations: _*))
        } yield updated
      )
      def locations(locations: Location*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.locations(locations: _*))
        } yield updated
      )
      def encoding(encoding: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.encoding(encoding))
        } yield updated
      )
      def encoding(encoding: Charset): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.encoding(encoding))
        } yield updated
      )
      def schemas(schemas: String*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.schemas(schemas: _*))
        } yield updated
      )
      def table(table: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.table(table))
        } yield updated
      )
      def target(target: MigrationVersion): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.target(target))
        } yield updated
      )
      def target(target: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.target(target))
        } yield updated
      )
      def placeholderReplacement(placeholderReplacement: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.placeholderReplacement(placeholderReplacement))
        } yield updated
      )
      def placeholders(placeholders: Map[String, String]): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.placeholders(placeholders))
        } yield updated
      )
      def placeholderPrefix(placeholderPrefix: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.placeholderPrefix(placeholderPrefix))
        } yield updated
      )
      def placeholderSuffix(placeholderSuffix: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.placeholderSuffix(placeholderSuffix))
        } yield updated
      )
      def sqlMigrationPrefix(sqlMigrationPrefix: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.sqlMigrationPrefix(sqlMigrationPrefix))
        } yield updated
      )
      def undoSqlMigrationPrefix(undoSqlMigrationPrefix: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.undoSqlMigrationPrefix(undoSqlMigrationPrefix))
        } yield updated
      )
      def repeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.repeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix))
        } yield updated
      )
      def sqlMigrationSeparator(sqlMigrationSeparator: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.sqlMigrationSeparator(sqlMigrationSeparator))
        } yield updated
      )
      def sqlMigrationSuffixes(sqlMigrationSuffixes: String*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.sqlMigrationSuffixes(sqlMigrationSuffixes))
        } yield updated
      )
      def connectRetries(connectRetries: Int): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.connectRetries(connectRetries))
        } yield updated
      )
      def initSql(initSql: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.initSql(initSql))
        } yield updated
      )
      def baselineVersion(baselineVersion: MigrationVersion): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.baselineVersion(baselineVersion))
        } yield updated
      )
      def baselineVersion(baselineVersion: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.baselineVersion(baselineVersion))
        } yield updated
      )
      def baselineDescription(baselineDescription: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.baselineDescription(baselineDescription))
        } yield updated
      )
      def baselineOnMigrate(baselineOnMigrate: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.baselineOnMigrate(baselineOnMigrate))
        } yield updated
      )
      def outOfOrder(outOfOrder: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.outOfOrder(outOfOrder))
        } yield updated
      )
      def callbacks(callbacks: Callback*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.callbacks(callbacks: _*))
        } yield updated
      )
      def callbacks(callbacks: String*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.callbacks(callbacks: _*))
        } yield updated
      )
      def skipDefaultCallbacks(skipDefaultCallbacks: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.skipDefaultCallbacks(skipDefaultCallbacks))
        } yield updated
      )
      def resolvers(resolvers: MigrationResolver*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.resolvers(resolvers: _*))
        } yield updated
      )
      def resolvers(resolvers: String*): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.resolvers(resolvers: _*))
        } yield updated
      )
      def skipDefaultResolvers(skipDefaultResolvers: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.skipDefaultResolvers(skipDefaultResolvers))
        } yield updated
      )
      def stream(stream: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.stream(stream))
        } yield updated
      )
      def batch(batch: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.batch(batch))
        } yield updated
      )
      def oracleSqlplus(oracleSqlplus: Boolean): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.oracleSqlplus(oracleSqlplus))
        } yield updated
      )
      def licenseKey(licenseKey: String): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.licenseKey(licenseKey))
        } yield updated
      )
      def configuration(properties: Properties): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.configuration(properties))
        } yield updated
      )
      def configuration(props: Map[String, String]): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.configuration(props))
        } yield updated
      )
      def envVars(): ConfigBuilder[F] = new ConfigBuilder[F](
        for {
          config <- c.f
          updated <- F.delay(config.envVars())
        } yield updated
      )

      def build: F[Configuration] = c.f.widen[Configuration]
    }
  }
}

object FlutterbyCats        {
  def fromConfig[F[_]](config: ConfigBuilder[F])(implicit F: Sync[F]): F[Flutterby[F]] =
    for {
      c      <- config.build
      flyway <- F.delay(Flyway.configure(c.getClassLoader).configuration(c).load())
    } yield new Flutterby[F] {
      override def baseline(): F[Unit]         = F.delay(flyway.baseline())
      override def migrate(): F[Int]           = F.delay(flyway.migrate())
      override def info(): F[AllMigrationInfo] =
        F.delay(flyway.info()) >>= ((i: FlywayMigrationInfoService) => AllMigrationInfoCats.fromFlyway[F](i))
      override def validate(): F[Unit]         = F.delay(flyway.validate())
      override def undo(): F[Int]              = F.delay(flyway.undo())
      override def repair(): F[Unit]           = F.delay(flyway.repair())
      override def clean(): F[Unit]            = F.delay(flyway.clean())
    }
}

object AllMigrationInfoCats {
  def fromFlyway[F[_]](f: FlywayMigrationInfoService)(implicit F: Sync[F]): F[AllMigrationInfo] =
    F.delay(
      AllMigrationInfo(
        all = f.all().toVector.map(MigrationInfo.fromFlyway),
        current = Option(f.current()).map(MigrationInfo.fromFlyway),
        pending = f.pending().toVector.map(MigrationInfo.fromFlyway),
        applied = f.applied().toVector.map(MigrationInfo.fromFlyway)
      )
    )
}
