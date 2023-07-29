package com.raunakjodhawat.nearby.controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import slick.jdbc.PostgresProfile.api._
import zio._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives.complete
import zio.http.Response
import zio.test.Assertion._
import zio.test._
import zio.test.akkahttp.AkkaZIOSpecDefault
import zio.test.junit.JUnitRunnableSpec

object ControllerSpec extends AnyFlatSpec with MockFactory {
  val mockDBZIO = ZIO.from(mock[Database])
  val controller = Controller(mockDBZIO)
  val vc = mock[VerificationController]
  val mockResponse: ZIO[Database, Throwable, Response] = ZIO.from(mock[Response])
  (vc.verifyUser _).expects(1L, "some-secret").returning(mockResponse)

}
class ControllerSpec extends JUnitRunnableSpec {
  import ControllerSpec._
  def spec = suite(
    test("verify user path") {
      assertCompletes(Get() ~> complete(HttpResponse()))(
        Assertion.isHisHandled(
          isResponse(equalTo(HttpResponse()))
        )
      )
    }
  )
}
