package database

import com.zaxxer.hikari.HikariDataSource
import config.DatabaseConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor
import zio.interop.catz.*
import zio.{Scope, Task, ZIO, ZLayer}

object TransactorLive:
  val layer: ZLayer[Scope & DatabaseConfig & FlywayMigrations, Throwable, HikariTransactor[Task]] = ZLayer:
    for
      _ <- ZIO.serviceWithZIO[FlywayMigrations](_.run)
      config <- ZIO.service[DatabaseConfig]
      ds <- ZIO.fromAutoCloseable(ZIO.attemptBlockingIO(createDataSource(config)))
      blockingExecutor <- ZIO.blockingExecutor
      transactor = HikariTransactor[Task](ds, blockingExecutor.asExecutionContext)
    yield transactor

  private def createDataSource(config: DatabaseConfig): HikariDataSource =
    val ds = new HikariDataSource()
    ds.setJdbcUrl(config.connectionUrl)
    ds.setUsername(config.user)
    ds.setPassword(config.password)
    ds
