package config

import com.typesafe.config.ConfigFactory
import zio.{ZIO, ZLayer}
import zio.config._
import zio.config.typesafe.TypesafeConfigSource
import zio.config.magnolia.descriptor

case class ApplicationConfig(database: DatabaseConfig)

object ApplicationConfig:
  private val config: ConfigDescriptor[ApplicationConfig] =
    descriptor[ApplicationConfig]
      .describe(s"ConfigDescriptor of ${classOf[ApplicationConfig].getCanonicalName}")

  lazy val applicationConfiglayer: ZLayer[Any, ReadError[String], ApplicationConfig] = ZLayer:
    val hoconSource: ConfigSource = TypesafeConfigSource.fromTypesafeConfig(
      ZIO.attempt(ConfigFactory.load())
    )

    read(config from hoconSource)

  lazy val databaseConfigLayer: ZLayer[Any, ReadError[String], DatabaseConfig] =
    applicationConfiglayer.project(_.database)
