package repository

import domain.Todo
import zio.{Task, URLayer, ZIO, ZLayer, mock}
import zio.mock.*

object TodoRepositoryMock extends Mock[TodoRepository]:
  object GetTodo extends Effect[String, Throwable, Option[Todo]]
  
  object GetTodos extends Effect[Unit, Throwable, Seq[Todo]]
  
  object CreateTodo extends Effect[(String, String), Throwable, Unit]
  
  object DeleteTodo extends Effect[String, Throwable, Unit]
  
  override val compose: URLayer[Proxy, TodoRepository] =
    ZLayer:
      ZIO.serviceWithZIO[Proxy] { proxy =>
        ZIO.succeed:
          new TodoRepository:
            override def getTodo(id: String): Task[Option[Todo]] =
              proxy(GetTodo, id)

            override def getTodos: Task[Seq[Todo]] =
              proxy(GetTodos)

            override def createTodo(id: String, description: String): Task[Unit] =
              proxy(CreateTodo, id, description)

            override def deleteTodo(id: String): Task[Unit] =
              proxy(DeleteTodo, id)
      }
