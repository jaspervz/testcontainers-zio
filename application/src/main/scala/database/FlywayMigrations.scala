package database

import config.DatabaseConfig
import org.flywaydb.core.Flyway
import zio.ZIO.logInfo
import zio.{Task, ZIO, ZLayer}

trait FlywayMigrations:
  def run: Task[Unit]

class FlywayMigrationsLive(config: DatabaseConfig) extends FlywayMigrations:
  override def run: Task[Unit] =
    for {
      _ <- logInfo("Starting migrations")
      flywayConfig =
          Flyway.configure
            .dataSource(config.connectionUrl, config.user, config.password)
            .validateMigrationNaming(true)
            .locations("database_migrations")
            .load
      _ <- ZIO.attemptBlocking(flywayConfig.migrate())
      _ <- logInfo("Finished migrations")
    } yield ()

object FlywayMigrationsLive {
  val layer: ZLayer[DatabaseConfig, Nothing, FlywayMigrations] = ZLayer.fromFunction(FlywayMigrationsLive(_))
}