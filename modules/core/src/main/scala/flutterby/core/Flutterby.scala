package flutterby.core

import java.time.Instant

import org.flywaydb.core.api.{
  MigrationState => FlywayMigrationState,
  MigrationType => FlywayMigrationType,
  MigrationVersion => FlywayMigrationVersion,
  MigrationInfo => FlywayMigrationInfo
}

import scala.concurrent.duration.FiniteDuration

trait Flutterby[F[_]] {
  def baseline: F[Unit]
  def migrate: F[Int]
  def info: F[MigrationInfoService[F]]
  def validate(): F[Unit]
  def undo(): F[Int]
  def repair(): F[Unit]
  def clean(): F[Unit]
}

trait MigrationInfoService[F[_]] {
  def all(): F[Vector[MigrationInfo]]
  def current(): F[Option[MigrationInfo]]
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

  def toFlyway(m: MigrationType): FlywayMigrationType = m match {
    case SCHEMA           => FlywayMigrationType.SCHEMA
    case BASELINE         => FlywayMigrationType.BASELINE
    case SQL              => FlywayMigrationType.SQL
    case UNDO_SQL         => FlywayMigrationType.UNDO_SQL
    case JDBC             => FlywayMigrationType.JDBC
    case UNDO_JDBC        => FlywayMigrationType.UNDO_JDBC
    case SPRING_JDBC      => FlywayMigrationType.SPRING_JDBC
    case UNDO_SPRING_JDBC => FlywayMigrationType.UNDO_SPRING_JDBC
    case CUSTOM           => FlywayMigrationType.CUSTOM
    case UNDO_CUSTOM      => FlywayMigrationType.UNDO_CUSTOM
  }
}
final case class DisplayText(value: String) extends AnyVal
final case class Version(value: String)
final case class MigrationVersion(version: Option[Version], displayText: DisplayText)
object MigrationVersion {
  val EMPTY = MigrationVersion(
    version = None,
    displayText = DisplayText("<< Empty Schema >>")
  )
  val LATEST = MigrationVersion(
    version = Some(Version(Long.MaxValue.toString)),
    displayText = DisplayText("<< Latest Version >>")
  )

  val CURRENT = {
    val currentVersionDisplayText = "<< Current Version >>"
    MigrationVersion(
      version = Some(Version(currentVersionDisplayText)),
      displayText = DisplayText(currentVersionDisplayText)
    )
  }

  def fromVersionString(version: Option[String]): MigrationVersion = version match {
    case None                                           => EMPTY
    case Some(v) if "current".equalsIgnoreCase(v)       => CURRENT
    case Some(v) if LATEST.version.exists(_.value == v) => LATEST
    case Some(v)                                        => MigrationVersion(Some(Version(v)), DisplayText(v))
  }

  def fromFlyway(f: FlywayMigrationVersion): MigrationVersion = f match {
    case FlywayMigrationVersion.EMPTY   => EMPTY
    case FlywayMigrationVersion.LATEST  => LATEST
    case FlywayMigrationVersion.CURRENT => CURRENT
    case other                          => fromVersionString(Some(other.toString))
  }

  def toFlyway(m: MigrationVersion): FlywayMigrationVersion = m match {
    case EMPTY                               => FlywayMigrationVersion.EMPTY
    case LATEST                              => FlywayMigrationVersion.LATEST
    case CURRENT                             => FlywayMigrationVersion.CURRENT
    case MigrationVersion(_, DisplayText(d)) => FlywayMigrationVersion.fromVersion(d)
  }

  implicit class MigrationVersionOps(val m: MigrationVersion) extends AnyVal {
    def toFlyway: FlywayMigrationVersion = MigrationVersion.toFlyway(m)
  }
}

final case class MigrationInfo(
    `type`: MigrationType,
    checksum: Checksum,
    version: MigrationVersion,
    description: Description,
    script: Script,
    state: MigrationState,
    installedOn: Option[InstalledOn],
    installedBy: Option[InstalledBy],
    installedRank: Option[InstalledRank],
    executionTime: Option[ExecutionTime]
)
object MigrationInfo {
  import scala.concurrent.duration._
  def fromFlyway(m: FlywayMigrationInfo): MigrationInfo = MigrationInfo(
    `type` = MigrationType.fromFlyway(m.getType),
    checksum = Checksum(m.getChecksum),
    version = MigrationVersion.fromFlyway(m.getVersion),
    description = Description(m.getDescription),
    script = Script(m.getScript),
    state = MigrationState.fromFlyway(m.getState),
    installedOn = Option(m.getInstalledOn).map(d => InstalledOn(d.toInstant)),
    installedBy = Option(m.getInstalledBy).map(InstalledBy.apply),
    installedRank = Option(m.getInstalledRank).map(r => InstalledRank(r.intValue())),
    executionTime = Option(m.getExecutionTime).map(e => ExecutionTime(e.intValue().millis))
  )
}
