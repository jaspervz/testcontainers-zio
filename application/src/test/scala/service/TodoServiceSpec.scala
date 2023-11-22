package service

import domain.Todo
import repository.TodoRepositoryMock
import zio.ZIO
import zio.mock.{Expectation, MockSpecDefault}
import zio.test.Assertion.{equalTo, isUnit}
import zio.test.{TestRandom, assert}

import java.util.UUID

object TodoServiceSpec extends MockSpecDefault:
  private val id = "id"

  private val description = "a description"

  def spec = suite("TodoService")(
    test("should create a todo"):
      val uuid = UUID.fromString("ccc71e1b-2b0d-4ce9-9155-138a7a2a685c")
      val layer = TodoRepositoryMock.CreateTodo(equalTo(uuid.toString, description), Expectation.unit).toLayer >>> TodoServiceLive.layer
      (for {
        _ <- TestRandom.feedUUIDs(uuid)
        service <- ZIO.service[TodoService]
        todo <- service.createTodo(description)
      } yield assert(todo)(equalTo(Todo(uuid.toString, description)))).provideLayer(layer),
    test("should remove a todo"):
      val layer = TodoRepositoryMock.DeleteTodo(equalTo(id), Expectation.unit).toLayer >>> TodoServiceLive.layer
      (for {
        service <- ZIO.service[TodoService]
        result <- service.deleteTodo(id)
      } yield assert(result)(isUnit)).provideLayer(layer),
    test("should return todos"):
      val todo1 = Todo("id 1", "description 1")
      val todo2 = Todo("id 2", "description 2")
      val layer = TodoRepositoryMock.GetTodos(Expectation.value(Seq(todo1, todo2))).toLayer >>> TodoServiceLive.layer
      (for {
        service <- ZIO.service[TodoService]
        result <- service.getTodos
      } yield assert(result)(equalTo(Seq(todo1, todo2)))).provideLayer(layer)
  )
