package service

import domain.Todo
import repository.TodoRepository
import zio.{Random, Task, ZLayer}

trait TodoService:
  def getTodo(id: String): Task[Option[Todo]]

  def getTodos: Task[Seq[Todo]]

  def createTodo(description: String): Task[Todo]

  def deleteTodo(id: String): Task[Unit]

case class TodoServiceLive(repository: TodoRepository) extends TodoService:
  override def getTodo(id: String): Task[Option[Todo]] =
    repository.getTodo(id)

  override def getTodos: Task[Seq[Todo]] =
    repository.getTodos

  override def createTodo(description: String): Task[Todo] =
    Random.nextUUID.flatMap { uuid =>
      val id = uuid.toString
      repository.createTodo(id, description).as(Todo(id, description))
    }

  override def deleteTodo(id: String): Task[Unit] =
    repository.deleteTodo(id)

object TodoServiceLive {
  val layer: ZLayer[TodoRepository, Nothing, TodoService] = ZLayer.fromFunction(TodoServiceLive(_))
}