package com.raunakjodhawat.nearby.controllers

import com.typesafe.slick.testkit.util.{ExternalJdbcTestDB, TestDB, Testkit}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import slick.jdbc.{JdbcProfile, PositionedResult, PostgresProfile}
import zio.http._

object ControllerSpec {
  implicit val conv: PositionedResult => (String, String, String, String) = { r =>
    (r.nextString(), r.nextString(), r.nextString(), r.nextString())
  }
  def tdb = new ExternalJdbcTestDB("mypostgres") {
    override def capabilities = super.capabilities - TestDB.capabilities.jdbcMetaGetFunctions

    override val profile: JdbcProfile = PostgresProfile
  }
}

@RunWith(classOf[Testkit])
class ControllerSpec extends AnyFlatSpec with MockFactory {

  import ControllerSpec.tdb
  "Controller" should "be able to be instantiated" in {
    val base_path: Path = Root / "test"
    val controller = Controller(base_path, tdb.createDB())
    assert(controller != null)
  }
}
