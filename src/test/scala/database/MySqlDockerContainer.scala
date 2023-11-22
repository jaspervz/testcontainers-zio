package database

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName
import zio.{Scope, ZIO, ZLayer}

case class MySqlDockerContainer(jdbcUrl: String, username: String, password: String)

object MySqlDockerContainer:

  def layer: ZLayer[Scope, Throwable, MySqlDockerContainer] = ZLayer:
    ZIO
      .fromAutoCloseable:
        ZIO.attemptBlocking:
          val container = new MySQLContainer(DockerImageName.parse("mysql:8.0.27"))
          container.start()
          container
      .map { container =>
        MySqlDockerContainer(container.getJdbcUrl, container.getUsername, container.getPassword)
      }
