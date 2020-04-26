package dev.busybyte.flutterby.cats

import cats.effect.Sync
import dev.busybyte.flutterby.core.{ Flutterby, MigrationInfoService }

object FlutterbyCats {
  def apply[F[_]: Sync]: Flutterby[F] = new Flutterby[F] {
    override def baseline: F[Unit]                = ???
    override def migrate: F[Int]                  = ???
    override def info: F[MigrationInfoService[F]] = ???
    override def validate(): F[Unit]              = ???
    override def undo(): F[Int]                   = ???
    override def repair(): F[Unit]                = ???
    override def clean(): F[Unit]                 = ???
  }
}
