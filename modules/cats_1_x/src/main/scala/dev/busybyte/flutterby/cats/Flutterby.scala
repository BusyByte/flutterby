package dev.busybyte.flutterby.cats

import cats.effect.Sync
import dev.busybyte.flutterby.core

object Flutterby {
  def apply[F[_]: Sync]: core.Flutterby[F] = new core.Flutterby[F] {
    override def baseline: F[Unit]                     = ???
    override def migrate: F[Int]                       = ???
    override def info: F[core.MigrationInfoService[F]] = ???
    override def validate(): F[Unit]                   = ???
    override def repair(): F[Unit]                     = ???
    override def clean(): F[Unit]                      = ???
  }
}
