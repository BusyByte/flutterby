package flutterby.core.config

import org.flywaydb.core.api.configuration.FluentConfiguration
import org.flywaydb.core.internal.license.FlywayProUpgradeRequiredException
import org.specs2.mutable.Specification
import com.github.ghik.silencer.silent

class FlutterbyConfigSpec extends Specification {
  "default config is the same" in {
    val converted                                 = FlutterbyConfig.toFlyway(FlutterbyConfig.defaultConfig, true).get
    val expected                                  = new FluentConfiguration()
    converted.getClassLoader must_== expected.getClassLoader
    converted.getDataSource must_== expected.getDataSource
    converted.getBaselineVersion must_== expected.getBaselineVersion
    converted.getBaselineDescription must_== expected.getBaselineDescription
    converted.getResolvers must haveSize(expected.getResolvers.size)
    converted.isSkipDefaultResolvers must_== expected.isSkipDefaultResolvers
    converted.getCallbacks must haveSize(expected.getCallbacks.size)
    converted.isSkipDefaultCallbacks must_== expected.isSkipDefaultCallbacks
    converted.getSqlMigrationPrefix must_== expected.getSqlMigrationPrefix
    converted.getUndoSqlMigrationPrefix must throwA[FlywayProUpgradeRequiredException]
    expected.getUndoSqlMigrationPrefix must throwA[FlywayProUpgradeRequiredException]
    converted.getRepeatableSqlMigrationPrefix must_== expected.getRepeatableSqlMigrationPrefix
    converted.getSqlMigrationSeparator must_== expected.getSqlMigrationSeparator
    converted.getSqlMigrationSuffixes.toList must_== expected.getSqlMigrationSuffixes.toList
    converted.isPlaceholderReplacement must_== expected.isPlaceholderReplacement
    converted.getPlaceholderSuffix must_== expected.getPlaceholderSuffix
    converted.getPlaceholderPrefix must_== expected.getPlaceholderPrefix
    converted.getPlaceholders must_== expected.getPlaceholders
    converted.getTarget must_== expected.getTarget
    converted.getTable must_== expected.getTable
    converted.getSchemas.toList must_== expected.getSchemas.toList
    converted.getEncoding must_== expected.getEncoding
    converted.getLocations.toList must_== expected.getLocations.toList
    converted.isBaselineOnMigrate must_== expected.isBaselineOnMigrate
    converted.isOutOfOrder must_== expected.isOutOfOrder
    converted.isIgnoreMissingMigrations must_== expected.isIgnoreMissingMigrations
    converted.isIgnoreFutureMigrations must_== expected.isIgnoreFutureMigrations
    converted.isValidateOnMigrate must_== expected.isValidateOnMigrate
    converted.isCleanOnValidationError must_== expected.isCleanOnValidationError
    converted.isCleanDisabled must_== expected.isCleanDisabled
    converted.isMixed must_== expected.isMixed
    converted.isGroup must_== expected.isGroup
    converted.getInstalledBy must_== expected.getInstalledBy
    @silent("deprecated") def assertErrorHandlers =
      converted.getErrorHandlers must throwA[FlywayProUpgradeRequiredException]
    assertErrorHandlers
    expected.getErrorHandlers must throwA[FlywayProUpgradeRequiredException]
    converted.getDryRunOutput must throwA[FlywayProUpgradeRequiredException]
    expected.getDryRunOutput must throwA[FlywayProUpgradeRequiredException]

    converted.getConnectRetries must_== expected.getConnectRetries
    converted.getInitSql must_== expected.getInitSql
    converted.isIgnoreIgnoredMigrations must_== expected.isIgnoreIgnoredMigrations
    converted.isIgnorePendingMigrations must_== expected.isIgnorePendingMigrations

    converted.getErrorOverrides must throwA[FlywayProUpgradeRequiredException]
    expected.getErrorOverrides must throwA[FlywayProUpgradeRequiredException]
    converted.isStream must throwA[FlywayProUpgradeRequiredException]
    expected.isStream must throwA[FlywayProUpgradeRequiredException]
    converted.isBatch must throwA[FlywayProUpgradeRequiredException]
    expected.isBatch must throwA[FlywayProUpgradeRequiredException]
    converted.isOracleSqlplus must throwA[FlywayProUpgradeRequiredException]
    expected.isOracleSqlplus must throwA[FlywayProUpgradeRequiredException]
    converted.getLicenseKey must throwA[FlywayProUpgradeRequiredException]
    expected.getLicenseKey must throwA[FlywayProUpgradeRequiredException]
  }
}
