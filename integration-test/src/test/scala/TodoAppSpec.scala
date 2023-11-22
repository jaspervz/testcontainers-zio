import docker.DockerCompose
import io.circe.literal.*
import zio.http.*
import zio.test.Assertion.isTrue
import zio.test.{Spec, ZIOSpecDefault, assert}
import zio.{Scope, ZIO}

object TodoAppSpec extends ZIOSpecDefault:
  def spec: Spec[Scope, Throwable] = suite("TodoApp")(
    test("should create a todo") {
      for {
        dockerCompose <- ZIO.service[DockerCompose]
        client <- ZIO.service[Client]
        url <- ZIO.fromEither(URL.decode(dockerCompose.applicationUrl))
        response <- client.url(url).post("/todos")(Body.fromString(json"""{"description":  "description"}""".toString, Charsets.Utf8).contentType(MediaType.application.json))
      } yield assert(response.status.isSuccess)(isTrue)
    }
  ).provideSomeLayerShared[Scope](DockerCompose.layer ++ Client.default)
