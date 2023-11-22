package database

import config.DatabaseConfig
import doobie.Transactor
import zio.{RLayer, Random, Scope, Task, ZIO, ZLayer}

object TestDatabase:
  private val mySqlDatabaseConfigLayer: RLayer[MySqlDockerContainer, DatabaseConfig] = ZLayer:
    ZIO.service[MySqlDockerContainer].map { container =>
      DatabaseConfig(
        connectionUrl = container.jdbcUrl,
        user = container.username,
        password = container.password
      )
    }

  private val h2DatabaseConfigLayer = ZLayer:
    Random.nextUUID.map { randomName =>
      DatabaseConfig(
        connectionUrl = s"jdbc:h2:mem:test_$randomName;MODE=MYSQL;DB_CLOSE_DELAY=-1",
        user = "sa",
        password = ""
      )
    }

  val mySqlLayer: ZLayer[Scope, Throwable, Transactor[Task]] =
    MySqlDockerContainer.layer >>> mySqlDatabaseConfigLayer >+> DatabaseLive.layer

  val h2Layer: ZLayer[Scope, Throwable, Transactor[Task]] =
    h2DatabaseConfigLayer >+> DatabaseLive.layer
