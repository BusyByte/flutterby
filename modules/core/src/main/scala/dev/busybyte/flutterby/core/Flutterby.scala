package dev.busybyte.flutterby.core

import java.time.Instant

import org.flywaydb.core.api.{
  MigrationState => FlywayMigrationState,
  MigrationType => FlywayMigrationType,
  MigrationVersion => FlywayMigrationVersion
}

import scala.concurrent.duration.FiniteDuration

trait Flutterby[F[_]] {
  def baseline: F[Unit]
  def migrate: F[Int]
  def info: F[MigrationInfoService[F]]
  def validate(): F[Unit]
  def repair(): F[Unit]
  def clean(): F[Unit]
}

trait MigrationInfoService[F[_]] {
  def all(): F[Vector[MigrationInfo]]
  def current(): F[MigrationInfo]
  def pending(): F[Vector[MigrationInfo]]
  def applied(): F[Vector[MigrationInfo]]
}

final case class DisplayName(value: String) extends AnyVal

sealed trait `IsResoved?`
object `IsResoved?` {
  case object YesResolved extends `IsResoved?`
  case object NotResolved extends `IsResoved?`
}

sealed trait `IsApplied?`
object `IsApplied?` {
  case object YesApplied extends `IsApplied?`
  case object NotApplied extends `IsApplied?`
}

sealed trait `Failed?`
object `Failed?` {
  case object YesFailed extends `Failed?`
  case object Successful extends `Failed?`
}
sealed abstract class MigrationState(
    val displayName: DisplayName,
    val resolved: `IsResoved?`,
    val applied: `IsApplied?`,
    val failed: `Failed?`
)
object MigrationState { //TODO: enumeratum
  case object PENDING
      extends MigrationState(
        DisplayName("Pending"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.NotApplied,
        `Failed?`.Successful
      )
  case object ABOVE_TARGET
      extends MigrationState(
        DisplayName("Above Target"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.NotApplied,
        `Failed?`.Successful
      )
  case object BELOW_BASELINE
      extends MigrationState(
        DisplayName("Below Baseline"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.NotApplied,
        `Failed?`.Successful
      )
  case object BASELINE
      extends MigrationState(
        DisplayName("Baseline"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )
  case object IGNORED
      extends MigrationState(
        DisplayName("Ignored"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.NotApplied,
        `Failed?`.Successful
      )
  case object MISSING_SUCCESS
      extends MigrationState(
        DisplayName("Missing"),
        `IsResoved?`.NotResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )
  case object MISSING_FAILED
      extends MigrationState(
        DisplayName("Failed (Missing)"),
        `IsResoved?`.NotResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.YesFailed
      )
  case object SUCCESS
      extends MigrationState(
        DisplayName("Success"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )
  case object UNDONE
      extends MigrationState(
        DisplayName("Undone"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )
  case object AVAILABLE
      extends MigrationState(
        DisplayName("Available"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.NotApplied,
        `Failed?`.Successful
      )
  case object FAILED
      extends MigrationState(
        DisplayName("Failed"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.YesFailed
      )
  case object OUT_OF_ORDER
      extends MigrationState(
        DisplayName("Out of Order"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )
  case object FUTURE_SUCCESS
      extends MigrationState(
        DisplayName("Future"),
        `IsResoved?`.NotResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )

  case object FUTURE_FAILED
      extends MigrationState(
        DisplayName("Failed (Future)"),
        `IsResoved?`.NotResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.YesFailed
      )

  case object OUTDATED
      extends MigrationState(
        DisplayName("Outdated"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )
  case object SUPERSEDED
      extends MigrationState(
        DisplayName("Superseded"),
        `IsResoved?`.YesResolved,
        `IsApplied?`.YesApplied,
        `Failed?`.Successful
      )

  def fromFlyway(f: FlywayMigrationState): MigrationState = f match {
    case FlywayMigrationState.PENDING         => PENDING
    case FlywayMigrationState.ABOVE_TARGET    => ABOVE_TARGET
    case FlywayMigrationState.BELOW_BASELINE  => BELOW_BASELINE
    case FlywayMigrationState.BASELINE        => BASELINE
    case FlywayMigrationState.IGNORED         => IGNORED
    case FlywayMigrationState.MISSING_SUCCESS => MISSING_SUCCESS
    case FlywayMigrationState.MISSING_FAILED  => MISSING_FAILED
    case FlywayMigrationState.SUCCESS         => SUCCESS
    case FlywayMigrationState.UNDONE          => UNDONE
    case FlywayMigrationState.AVAILABLE       => AVAILABLE
    case FlywayMigrationState.FAILED          => FAILED
    case FlywayMigrationState.OUT_OF_ORDER    => OUT_OF_ORDER
    case FlywayMigrationState.FUTURE_SUCCESS  => FUTURE_SUCCESS
    case FlywayMigrationState.FUTURE_FAILED   => FUTURE_FAILED
    case FlywayMigrationState.OUTDATED        => OUTDATED
    case FlywayMigrationState.SUPERSEDED      => SUPERSEDED
  }
}

final case class Checksum(value: Int) extends AnyVal
final case class Description(value: String) extends AnyVal
final case class Script(value: String) extends AnyVal
final case class InstalledOn(value: Instant)
final case class InstalledBy(value: String) extends AnyVal
final case class InstalledRank(value: Int) extends AnyVal
final case class ExecutionTime(value: FiniteDuration)

sealed trait `IsSynthetic?`
object `IsSynthetic?` {
  case object Synthetic extends `IsSynthetic?`
  case object Real extends `IsSynthetic?`
}

sealed trait `IsUndo?`
object `IsUndo?` {
  case object YesUndo extends `IsUndo?`
  case object NotUndo extends `IsUndo?`
}
sealed abstract class MigrationType(
    val synthetic: `IsSynthetic?`,
    val undo: `IsUndo?`
)
object MigrationType {
  case object SCHEMA extends MigrationType(`IsSynthetic?`.Synthetic, `IsUndo?`.NotUndo)
  case object BASELINE extends MigrationType(`IsSynthetic?`.Synthetic, `IsUndo?`.NotUndo)
  case object SQL extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.NotUndo)
  case object UNDO_SQL extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.YesUndo)
  case object JDBC extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.NotUndo)
  case object UNDO_JDBC extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.YesUndo)
  case object SPRING_JDBC extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.NotUndo)
  case object UNDO_SPRING_JDBC extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.YesUndo)
  case object CUSTOM extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.NotUndo)
  case object UNDO_CUSTOM extends MigrationType(`IsSynthetic?`.Real, `IsUndo?`.YesUndo)

  def fromFlyway(f: FlywayMigrationType): MigrationType = f match {
    case FlywayMigrationType.SCHEMA           => SCHEMA
    case FlywayMigrationType.BASELINE         => BASELINE
    case FlywayMigrationType.SQL              => SQL
    case FlywayMigrationType.UNDO_SQL         => UNDO_SQL
    case FlywayMigrationType.JDBC             => JDBC
    case FlywayMigrationType.UNDO_JDBC        => UNDO_JDBC
    case FlywayMigrationType.SPRING_JDBC      => SPRING_JDBC
    case FlywayMigrationType.UNDO_SPRING_JDBC => UNDO_SPRING_JDBC
    case FlywayMigrationType.CUSTOM           => CUSTOM
    case FlywayMigrationType.UNDO_CUSTOM      => UNDO_CUSTOM
  }
}
final case class DisplayText(value: String) extends AnyVal
final case class Version(value: String)
sealed trait MigrationVersion {
  def version: Option[Version]
  def displayText: DisplayText
}
object MigrationVersion {
  case object EMPTY extends MigrationVersion {
    val version: Option[Version] = None
    val displayText: DisplayText = DisplayText("<< Empty Schema >>")
  }
  case object LATEST extends MigrationVersion {
    val version: Option[Version] = Some(Version(Long.MaxValue.toString))
    val displayText: DisplayText = DisplayText("<< Latest Version >>")
  }
  case object CURRENT extends MigrationVersion { //TODO these need specs to make sure
    val version: Option[Version] = Some(Version("<< Current Version >>"))
    val displayText: DisplayText = DisplayText("<< Current Version >>")
  }

  final case class NormalMigrationVersion(displayText: DisplayText) extends MigrationVersion {
    val version: Option[Version] = Some(Version(displayText.value))
  }

  def fromFlyway(f: FlywayMigrationVersion): MigrationVersion = f match {
    case FlywayMigrationVersion.EMPTY   => EMPTY
    case FlywayMigrationVersion.LATEST  => LATEST
    case FlywayMigrationVersion.CURRENT => CURRENT
    case other                          => NormalMigrationVersion(DisplayText(other.toString))
  }
}

sealed trait MigrationInfo {
  def `type`: MigrationType
  def checksum: Checksum
  def version: MigrationVersion
  def description: Description
  def script: Script
  def state: MigrationState
}

final case class AppliedMigrationInfo(
    `type`: MigrationType,
    checksum: Checksum,
    version: MigrationVersion,
    description: Description,
    script: Script,
    state: MigrationState,
    installedOn: InstalledOn,
    installedBy: InstalledBy,
    installedRank: InstalledRank,
    executionTime: ExecutionTime
) extends MigrationInfo

final case class UnAppliedMigrationInfo(
    `type`: MigrationType,
    checksum: Checksum,
    version: MigrationVersion,
    description: Description,
    script: Script,
    state: MigrationState
) extends MigrationInfo
