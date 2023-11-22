package database

import config.DatabaseConfig
import doobie.Transactor
import zio.{Scope, Task, ZLayer}

object DatabaseLive:
  val layer: ZLayer[Scope & DatabaseConfig, Throwable, Transactor[Task]] =
    FlywayMigrationsLive.layer >>> TransactorLive.layer
