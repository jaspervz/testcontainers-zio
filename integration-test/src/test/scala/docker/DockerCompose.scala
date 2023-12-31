package docker

import org.testcontainers.containers.DockerComposeContainer
import zio.{Scope, ZIO, ZLayer}

import java.io.File
import scala.util.Properties

case class DockerCompose(applicationUrl: String)


object DockerCompose:
  private val serviceName = "application"

  private val servicePort = 8080

  val layer: ZLayer[Scope, Throwable, DockerCompose] = ZLayer:
    ZIO.fromAutoCloseable {
      ZIO.attemptBlocking:
        val containers = DockerComposeContainer(File(getClass.getResource("/docker-compose.yml").getFile))
        containers.withExposedService(serviceName, servicePort)
        val serviceTag = Properties.propOrElse("build", "latest")
        containers.withEnv("BUILD_TAG", serviceTag)
        containers.start()
        containers
    }.map { containers =>
      val host = containers.getServiceHost(serviceName, servicePort)
      val port = containers.getServicePort(serviceName, servicePort)
      DockerCompose(applicationUrl = s"http://$host:$port")
    }
