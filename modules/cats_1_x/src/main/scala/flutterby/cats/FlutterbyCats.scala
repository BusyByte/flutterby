package flutterby.cats

import cats.effect.Sync
import flutterby.core.{AllMigrationInfo, Flutterby, MigrationInfo}
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.{MigrationInfoService => FlywayMigrationInfoService}
import cats.implicits._
import flutterby.cats.config.ConfigBuilder

object FlutterbyCats        {
  def fromConfig[F[_]](config: ConfigBuilder[F])(
      implicit F: Sync[F]
  ): F[Flutterby[F]] =
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
