package repository

import database.TestDatabase
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

object TodoRepositoryH2Spec extends ZIOSpecDefault:
  def spec: Spec[TestEnvironment & Scope, Throwable] = TodoRepositoryTest.spec.provideLayerShared(TestDatabase.h2Layer)
