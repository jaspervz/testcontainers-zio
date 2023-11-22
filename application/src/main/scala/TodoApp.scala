import api.TodoApi
import config.ApplicationConfig
import database.DatabaseLive
import repository.TodoRepositoryLive
import zio.{Scope, ZIOAppDefault}
import zio.http.Server
import service.TodoServiceLive

object TodoApp extends ZIOAppDefault:
  override def run =
    Server.serve(TodoApi.routes.toHttpApp).provideSome[Scope](
      ApplicationConfig.databaseConfigLayer,
      DatabaseLive.layer,
      Server.default,
      TodoServiceLive.layer,
      TodoRepositoryLive.layer
    )
