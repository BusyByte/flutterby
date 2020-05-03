package flutterby.core

import java.time.Instant

import org.flywaydb.core.api.{
  MigrationInfo => FlywayMigrationInfo,
  MigrationState => FlywayMigrationState,
  MigrationType => FlywayMigrationType,
  MigrationVersion => FlywayMigrationVersion
}

import scala.concurrent.duration.FiniteDuration

trait Flutterby[F[_]] {
  def baseline(): F[Unit]
  def migrate(): F[Int]
  def info(): F[AllMigrationInfo]
  def validate(): F[Unit]
  def undo(): F[Int]
  def repair(): F[Unit]
  def clean(): F[Unit]
}

final case class AllMigrationInfo(
    all: Vector[MigrationInfo],
    current: Option[MigrationInfo],
    pending: Vector[MigrationInfo],
    applied: Vector[MigrationInfo]
)

sealed abstract class MigrationVersion(private val m: FlywayMigrationVersion) extends Comparable[MigrationVersion] {
  def version: Option[String]
  def atLeast(otherVersion: String): Boolean
  def newerThan(otherVersion: String): Boolean
  def majorNewerThan(otherVersion: String): Boolean
  def major(): BigInt
  def majorAsString(): String
  def minorAsString(): String
}
object MigrationVersion {
  def fromFlyway(m: FlywayMigrationVersion): MigrationVersion =
    new MigrationVersion(m) {
      override def version: Option[String]                       = Option(m.getVersion)
      override def atLeast(otherVersion: String): Boolean        = m.isAtLeast(otherVersion)
      override def newerThan(otherVersion: String): Boolean      = m.isNewerThan(otherVersion)
      override def majorNewerThan(otherVersion: String): Boolean = m.isMajorNewerThan(otherVersion)
      override def major(): BigInt                               = m.getMajor
      override def majorAsString(): String                       = m.getMajorAsString
      override def minorAsString(): String                       = m.getMinorAsString
      override def compareTo(o: MigrationVersion): Int           = m.compareTo(o.m)
      override def toString: String                              = m.toString
      override def hashCode(): Int                               = m.hashCode()
      override def equals(obj: Any): Boolean                     = m.equals(obj)
    }
}

sealed abstract class MigrationInfo(private val m: FlywayMigrationInfo)       extends Comparable[MigrationInfo] {
  def `type`: FlywayMigrationType
  def checksum: Integer
  def version: MigrationVersion
  def description: String
  def script: String
  def state: FlywayMigrationState
  def installedOn: Option[Instant]
  def installedBy: Option[String]
  def installedRank: Option[Int]
  def executionTime: Option[FiniteDuration]
}
object MigrationInfo    {
  import scala.concurrent.duration._
  def fromFlyway(m: FlywayMigrationInfo): MigrationInfo =
    new MigrationInfo(m) {
      override def `type`: FlywayMigrationType           = m.getType
      override def checksum: Integer                     = m.getChecksum
      override def version                               = MigrationVersion.fromFlyway(m.getVersion)
      override def description: String                   = m.getDescription
      override def script: String                        = m.getScript
      override def state: FlywayMigrationState           = m.getState
      override def installedOn: Option[Instant]          = Option(m.getInstalledOn).map(d => d.toInstant)
      override def installedBy: Option[String]           = Option(m.getInstalledBy)
      override def installedRank: Option[Int]            = Option(m.getInstalledRank).map(r => r.intValue())
      override def executionTime: Option[FiniteDuration] = Option(m.getExecutionTime).map(e => e.intValue().millis)
      override def compareTo(o: MigrationInfo): Int      = m.compareTo(o.m)
    }
}
