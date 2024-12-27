import sbt.Keys.testFrameworks

import scala.util.Properties

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val zioVersion = "2.0.19"

lazy val zioConfigVersion = "3.0.7"

lazy val flywayVersion = "11.1.0"

lazy val circeVersion = "0.14.6"

lazy val testContainersVersion = "1.19.2"

lazy val zioDependencies = Seq(
  "dev.zio" %% "zio"              % zioVersion
)

lazy val zioHttpDependencies = Seq(
  "dev.zio" %% "zio-http"         % "3.0.0-RC3"
)

lazy val zioTestDependencies = Seq(
  "dev.zio" %% "zio-test"          % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt"      % "2.0.19" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.0.19" % Test,
  "dev.zio" %% "zio-mock"          % "1.0.0-RC12" % Test
)

lazy val zioConfigDependencies = Seq(
  "dev.zio" %% "zio-config"          % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion
)

lazy val circeDependencies = Seq(
  "io.circe" %% "circe-core"    % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser"  % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion
)

lazy val flywayDependencies = Seq(
  "org.flywaydb" % "flyway-core"  % flywayVersion,
  "org.flywaydb" % "flyway-mysql" % flywayVersion
)

lazy val testContainersDependencies = Seq(
  "org.testcontainers" % "testcontainers" % testContainersVersion % Test,
  "org.testcontainers" % "mysql"          % testContainersVersion % Test
)

lazy val loggingDependencies = Seq(
  "org.slf4j"      % "slf4j-api"       % "2.0.9",
  "ch.qos.logback" % "logback-classic" % "1.4.11"
)

lazy val databaseDependencies = Seq(
  "dev.zio"       %% "zio-interop-cats"  % "23.1.0.0",
  "org.tpolecat"  %% "doobie-hikari"     % "1.0.0-RC4",
  "com.mysql"      % "mysql-connector-j" % "8.2.0",
  "com.h2database" % "h2" % "2.2.224"    % Test
)

lazy val root = (project in file("."))
  .aggregate(application)
  .settings(
    commands += Command.command("integrationTest") { state =>
      "Docker/publishLocal" ::
      "project integration-test" ::
      "test" ::
      state
    }
  )

lazy val application = (project in file("application"))
  .enablePlugins(
    DockerPlugin,
    JavaAppPackaging
  )
  .settings(
    name := "testcontainers-zio",
    libraryDependencies ++=
      zioDependencies ++
      zioConfigDependencies ++
      zioHttpDependencies ++
      zioTestDependencies ++
      circeDependencies ++
      flywayDependencies ++
      databaseDependencies ++
      loggingDependencies ++
      testContainersDependencies,
    Test / run / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    Compile / packageDoc / publishArtifact := false,
    scalacOptions ++= Seq("-indent", "-rewrite"),
    Docker / packageName := "application",
    dockerBaseImage := "eclipse-temurin:19.0.2_7-jdk",
    dockerExposedPorts += 8080,
    dockerAliases ++= Seq(Properties.propOrNone("build"), Some("latest")).flatten.map(tag => dockerAlias.value.withTag(Some(tag)))
  )

lazy val `integration-test` = (project in file("integration-test"))
  .settings(
    scalacOptions ++= Seq("-indent", "-rewrite"),
    libraryDependencies ++=
      zioHttpDependencies ++
      zioTestDependencies ++
      circeDependencies ++
      loggingDependencies ++
      testContainersDependencies
  )
