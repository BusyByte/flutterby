package dev.busybyte.flutterby.core

import java.time.Instant

import org.flywaydb.core.api.{ MigrationState, MigrationType, MigrationVersion }

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

final case class Checksum(value: Int) extends AnyVal
final case class Description(value: String) extends AnyVal
final case class Script(value: String) extends AnyVal
final case class InstalledOn(value: Instant)
final case class InstalledBy(value: String) extends AnyVal
final case class InstalledRank(value: Int) extends AnyVal
final case class ExecutionTime(value: FiniteDuration)

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
