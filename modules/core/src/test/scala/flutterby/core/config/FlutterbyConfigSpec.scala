package flutterby.core.config

import com.github.ghik.silencer.silent
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.license.FlywayProUpgradeRequiredException
import org.specs2.mutable.Specification

class FlutterbyConfigSpec extends Specification {
  "default config is the same" in {
    val converted = FlutterbyConfig.toFlyway(FlutterbyConfig.defaultConfig)
    val expected  = Flyway.configure()
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

    @silent("deprecated") def assertDeprecatedErrorHandlers =
      converted.getErrorHandlers must throwA[FlywayProUpgradeRequiredException]
    assertDeprecatedErrorHandlers
    expected.getErrorHandlers must throwA[FlywayProUpgradeRequiredException]
    converted.getDryRunOutput must throwA[FlywayProUpgradeRequiredException]
    expected.getDryRunOutput must throwA[FlywayProUpgradeRequiredException]
  }
}
