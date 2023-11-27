package database

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName
import zio.{Scope, ZIO, ZLayer}

import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ArrayBuffer

case class AerospikeContainer(host: String, port: Int)

object AerospikeContainer:
  private val AeroSpikePort: Int = 3000

  val layer: ZLayer[Scope, Throwable, AerospikeContainer] = ZLayer:
    ZIO
      .fromAutoCloseable:
        ZIO.attemptBlocking:
          val container = new GenericContainer(DockerImageName.parse("aerospike:ce-7.0.0.0"))
          container.setExposedPorts(ArrayBuffer(Integer.valueOf(3000)).asJava)
          container.setWaitStrategy(new LogMessageWaitStrategy().withRegEx(".*migrations: complete.*\\s"))
          container.start()
          container
      .map { container =>
        AerospikeContainer(container.getHost, container.getMappedPort(AeroSpikePort))
      }

