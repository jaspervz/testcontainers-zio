package repository

import domain.Todo
import doobie.Transactor
import zio.test.*
import zio.test.Assertion.{equalTo, hasSameElements, hasSubset, isNone, isSome}
import zio.{Random, Task, ZIO}

object TodoRepositoryTest:
  private val description = "a description"

  def spec = suite("TodoRepository")(
    test("should create a todo"):
      for
        repository <- ZIO.service[TodoRepository]
        id <- live(Random.nextUUID.map(_.toString))
        _ <- repository.createTodo(id, description)
        todo <- repository.getTodo(id)
        _ <- repository.deleteTodo(id)
      yield assert(todo)(isSome(equalTo(Todo(id, description)))),
    test("should remove a todo"):
      for
        repository <- ZIO.service[TodoRepository]
        id <- live(Random.nextUUID.map(_.toString))
        _ <- repository.createTodo(id, description)
        todoBeforeDelete <- repository.getTodo(id)
        _ <- repository.deleteTodo(id)
        todoAfterDelete <- repository.getTodo(id)
      yield assert(todoBeforeDelete)(isSome(equalTo(Todo(id, description)))) && assert(todoAfterDelete)(isNone),
    test("should get todos"):
      for
        repository <- ZIO.service[TodoRepository]
        id1 <- live(Random.nextUUID.map(_.toString))
        _ <- repository.createTodo(id1, "description 1")
        id2 <- live(Random.nextUUID.map(_.toString))
        _ <- repository.createTodo(id2, "description 2")
        todos <- repository.getTodos
      yield assert(todos)(hasSubset(Seq(Todo(id1, "description 1"), Todo(id2, "description 2"))))
  ).provideSomeLayer[Transactor[Task]](TodoRepositoryLive.layer)
end TodoRepositoryTest
