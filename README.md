# Testcontainers ZIO
This is a sample project that shows how to use [Testcontainers](https://testcontainers.com/) for (integration) tests
in combination with [ZIO](https://zio.dev/).

## Testcontainers
[Testcontainers](https://testcontainers.com/) provides a way to run a Docker container with e.g. a database for testing.
It is also possible to run a docker-compose for testing.

If the Docker container exposes a port, it will map this exposed port to a 
free port number on the external host, avoiding port conflicts. In your test you can
retrieve the mapping port number so you can connect to the Docker container(s).

## Sample project
The sample project is a simple REST API for to do items with a create, delete, get, and
get all endpoint.

Besides that, it contains the example [AerospikeContainer](application/src/test/scala/database/AerospikeContainer.scala) on how to use
Testcontainers' `GenericContainer` for when there is
no container available in the Testcontainers project.

### Repository
The [`TodoRepository`](application/src/main/scala/repository/TodoRepository.scala) is responsible for storing the to do's in the database.
`TodoRepositoryTest` contains the tests for the repository. There are different Specs
that use `TodoRepositoryTest`, each injecting different layers for the database:

| Spec                                                                                                                         | Description                                                                                                             |
|------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| [`TodoRepositoryMySqlLayerSharedSpec`](application/src/test/scala/repository/TodoRepositoryMySqlLayerSharedSpec.scala)       | uses a shared MySQL Docker container for the tests                                                                      |
| [`TodoRepositoryMySqlLayerNonSharedSpec`](application/src/test/scala/repository/TodoRepositoryMySqlLayerNonSharedSpec.scala) | uses a non-shared MySQL Docker container for the test, this means that for every test a new Docker container is started |
| [`TodoRepositoryH2Spec`](application/src/test/scala/repository/TodoRepositoryH2Spec.scala)                                   | uses a H2 database instead of a Docker container, which is faster, but only an emulation of MySQL                       |

[`MysqlDockerContainer`](application/src/test/scala/database/MySqlDockerContainer.scala) shows how to start a MySQL docker container and
obtain the correct jdbc connection url, username, and password to connect to the MySQL instance.

[`TestDatabase`](application/src/test/scala/database/TestDatabase.scala) contains two test layers: one for MySQL and one for H2, to be able
to switch easily between the two test databases.

### Service
The [`TodoService`](application/src/main/scala/service/TodoService.scala) is a lightweight service that doesn't do a lot, it is added to
show how to test such a service that depends on a repository that depends on a database
without requiring a database. This is done in [`TodoServiceSpec`](application/src/test/scala/service/TodoServiceSpec.scala) using [ZIO mock](https://github.com/zio/zio-mock).

### Api and application
The [`TodoApi`](application/src/main/scala/api/TodoApi.scala) defines the routes for the REST API and [`TodoApp`](application/src/main/scala/TodoApp.scala) is the application.

## Integration test
The integration test isn't very extensive, it is meant to show how to test the
entire application using a docker compose.

The command `sbt integrationTest` will build a Docker container of the application
and will run the test(s) in the [`integration-test`](integration-test) project.

[`TodoAppSpec`](integration-test/src/test/scala/TodoAppSpec.scala) tests the application. [`DockerCompose`](integration-test/src/test/scala/docker/DockerCompose.scala) starts the docker compose file, which
contains the application and the database. It retrieves the mapped host and port and
composes the URL for the application so we can use a HTTP client to connect
the application's REST end points.