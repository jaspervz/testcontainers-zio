import sbt.Keys.testFrameworks

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

scalacOptions ++= Seq("-indent", "-rewrite")

lazy val zioVersion = "2.0.19"

lazy val zioConfigVersion = "3.0.7"

lazy val flywayVersion = "9.22.3"

lazy val circeVersion = "0.14.1"

lazy val testContainersVersion = "1.19.2"

lazy val root = (project in file("."))
  .settings(
    name := "testcontainers-zio",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"              % zioVersion,
      "dev.zio" %% "zio-interop-cats" % "23.1.0.0",
      "dev.zio" %% "zio-http"         % "3.0.0-RC3",

      "dev.zio" %% "zio-config"          % zioConfigVersion,
      "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,

      "dev.zio" %% "zio-test"          % zioVersion   % Test,
      "dev.zio" %% "zio-test-sbt"      % "2.0.19"     % Test,
      "dev.zio" %% "zio-test-magnolia" % "2.0.19"     % Test,
      "dev.zio" %% "zio-mock"          % "1.0.0-RC12" % Test,

      "org.tpolecat"  %% "doobie-hikari" % "1.0.0-RC4",

      "io.circe" %% "circe-core"    % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser"  % circeVersion,

      "org.flywaydb" % "flyway-core"  % flywayVersion,
      "org.flywaydb" % "flyway-mysql" % flywayVersion,

      "org.slf4j"      % "slf4j-api"       % "2.0.9",
      "ch.qos.logback" % "logback-classic" % "1.4.11",

      "com.mysql"      % "mysql-connector-j" % "8.2.0",
      "com.h2database" % "h2"                % "2.2.224" % Test,

      "org.testcontainers" % "testcontainers" % testContainersVersion % Test,
      "org.testcontainers" % "mysql"          % testContainersVersion % Test,
    ),
    Test / run / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
