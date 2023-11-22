package repository

import database.TestDatabase
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

object TodoRepositoryMySqlLayerSharedSpec extends ZIOSpecDefault:
  def spec: Spec[TestEnvironment & Scope, Throwable] = TodoRepositoryTest.spec.provideLayerShared(TestDatabase.mySqlLayer)
