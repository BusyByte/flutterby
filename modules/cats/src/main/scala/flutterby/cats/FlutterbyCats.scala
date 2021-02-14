package flutterby.cats

import cats.effect.Sync
import flutterby.core.{AllMigrationInfo, Flutterby, MigrationInfo}
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.{MigrationInfoService => FlywayMigrationInfoService}
import cats.implicits._
import flutterby.cats.config.Config

object FlutterbyCats {
  def fromConfig[F[_]](config: Config[F])(
      implicit F: Sync[F]
  ): F[Flutterby[F]] =
    for {
      c      <- config.config
      flyway <- F.delay(Flyway.configure(c.getClassLoader).configuration(c).load())
    } yield new Flutterby[F] {
      override def baseline(): F[Unit]         = F.delay(flyway.baseline()).void              // TODO: support new model instead of void
      override def migrate(): F[Int]           = F.delay(flyway.migrate().migrationsExecuted) // TODO: support new model
      override def info(): F[AllMigrationInfo] =
        F.delay(flyway.info()) >>= ((i: FlywayMigrationInfoService) => AllMigrationInfoCats.fromFlyway[F](i))
      override def validate(): F[Unit]         = F.delay(flyway.validate())
      override def undo(): F[Int]              = F.delay(flyway.undo().migrationsUndone)      // TODO: support new model
      override def repair(): F[Unit]           = F.delay(flyway.repair()).void                // TODO: support new model instead of void
      override def clean(): F[Unit]            = F.delay(flyway.clean()).void                 // TODO: support new model instead of void
    }
}

object AllMigrationInfoCats {
  def fromFlyway[F[_]](f: FlywayMigrationInfoService)(
      implicit F: Sync[F]
  ): F[AllMigrationInfo] =
    F.delay(
      AllMigrationInfo(
        all = f.all().toVector.map(MigrationInfo.fromFlyway),
        current = Option(f.current()).map(MigrationInfo.fromFlyway),
        pending = f.pending().toVector.map(MigrationInfo.fromFlyway),
        applied = f.applied().toVector.map(MigrationInfo.fromFlyway)
      )
    )
}
