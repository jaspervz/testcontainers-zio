package repository

import database.TestDatabase
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

object TodoRepositoryMySqlLayerNonSharedSpec extends ZIOSpecDefault:
  def spec: Spec[TestEnvironment & Scope, Throwable] = TodoRepositoryTest.spec.provideLayer(TestDatabase.mySqlLayer)
