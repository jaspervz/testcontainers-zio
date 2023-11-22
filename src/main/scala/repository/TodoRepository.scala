package repository

import domain.Todo
import doobie.Transactor
import zio.{Task, ZLayer}
import doobie.implicits.*
import zio.interop.catz._

trait TodoRepository:
  def getTodo(id: String): Task[Option[Todo]]

  def getTodos: Task[Seq[Todo]]

  def createTodo(id: String, description: String): Task[Unit]

  def deleteTodo(id: String): Task[Unit]
end TodoRepository

class TodoRepositoryLive(transactor: Transactor[Task]) extends TodoRepository:
  override def getTodo(id: String): Task[Option[Todo]] =
    sql"SELECT id, description FROM todos WHERE id = $id"
      .query[Todo]
      .option
      .transact(transactor)

  override def getTodos: Task[Seq[Todo]] =
    sql"SELECT id, description FROM todos"
      .query[Todo]
      .to[List]
      .transact(transactor)

  override def createTodo(id: String, description: String): Task[Unit] =
    sql"INSERT INTO todos (id, description) VALUES ($id, $description)"
      .update
      .run
      .transact(transactor)
      .unit

  override def deleteTodo(id: String): Task[Unit] =
    sql"DELETE FROM todos WHERE id = $id"
      .update
      .run
      .transact(transactor)
      .unit
end TodoRepositoryLive

object TodoRepositoryLive:
  val layer: ZLayer[Transactor[Task], Nothing, TodoRepositoryLive] = ZLayer.fromFunction(TodoRepositoryLive(_))
end TodoRepositoryLive
