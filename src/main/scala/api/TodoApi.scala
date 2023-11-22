package api

import domain.Todo
import io.circe.syntax.*
import io.circe.generic.auto.*
import io.circe.{DecodingFailure, parser}
import service.TodoService
import zio.ZIO
import zio.http.*

object TodoApi:
   val routes: Routes[TodoService, Nothing] = Routes(
      Method.GET / "todos" / string("id") ->
        handler { (id: String, _: Request) =>
           for {
              todoService <- ZIO.service[TodoService]
              mayBeTodo <- todoService.getTodo(id)
              response = mayBeTodo.map(todo => Response.json(todo.asJson.toString)).getOrElse(Response.notFound)
           } yield response
        },
     Method.GET / "todos" ->
       handler {
         for {
           todoService <- ZIO.service[TodoService]
           todos <- todoService.getTodos
           response = Response.json(todos.asJson.toString)
         } yield response
       },
     Method.DELETE / "todos" / string("id") ->
       handler { (id: String, _: Request) =>
         for {
           todoService <- ZIO.service[TodoService]
           _ <- todoService.deleteTodo(id)
         } yield Response.ok
       },
    Method.POST / "todos" ->
      handler { (req: Request) =>
        for {
          body <- req.body.asString
          json <- ZIO.fromEither(parser.parse(body))
          createTodo <- ZIO.fromEither(json.as[CreateTodo])
          todoService <- ZIO.service[TodoService]
          createdTodo <- todoService.createTodo(createTodo.description)
        } yield Response.json(createdTodo.asJson.toString)
      }
   ).handleError:
     case error: DecodingFailure => Response.badRequest(error.getMessage)
     case other => Response.internalServerError(other.getMessage)
