package flutterby.cats

import cats.effect.Sync
import flutterby.core.{Flutterby, MigrationInfo, MigrationInfoService}
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.{MigrationInfoService => FlywayMigrationInfoService}
import cats.implicits._
import org.flywaydb.core.api.configuration.FluentConfiguration

object FlutterbyCats            {
  def fromConfig[F[_]](c: FluentConfiguration)(implicit F: Sync[F]): F[Flutterby[F]] =
    for {
      flyway <- F.delay(Flyway.configure(c.getClassLoader).load())
    } yield new Flutterby[F] {
      override def baseline(): F[Unit]                = F.delay(flyway.baseline())
      override def migrate(): F[Int]                  = F.delay(flyway.migrate())
      override def info(): F[MigrationInfoService[F]] = F.delay(MigrationInfoServiceCats.fromFlyway(flyway.info()))
      override def validate(): F[Unit]                = F.delay(flyway.validate())
      override def undo(): F[Int]                     = F.delay(flyway.undo())
      override def repair(): F[Unit]                  = F.delay(flyway.repair())
      override def clean(): F[Unit]                   = F.delay(flyway.clean())
    }

  def fromDefault[F[_]: Sync](): F[Flutterby[F]]                                     =
    fromConfig(Flyway.configure())
}

object MigrationInfoServiceCats {
  def fromFlyway[F[_]](f: FlywayMigrationInfoService)(implicit F: Sync[F]): MigrationInfoService[F] =
    new MigrationInfoService[F] {
      override def all(): F[Vector[MigrationInfo]]     = F.delay(f.all().toVector.map(MigrationInfo.fromFlyway))
      override def current(): F[Option[MigrationInfo]] = F.delay(Option(f.current()).map(MigrationInfo.fromFlyway))
      override def pending(): F[Vector[MigrationInfo]] = F.delay(f.pending().toVector.map(MigrationInfo.fromFlyway))
      override def applied(): F[Vector[MigrationInfo]] = F.delay(f.applied().toVector.map(MigrationInfo.fromFlyway))
    }
}
