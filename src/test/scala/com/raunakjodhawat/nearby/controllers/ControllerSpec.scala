package com.raunakjodhawat.nearby.controllers

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import slick.jdbc.PositionedResult

object ControllerSpec {
  implicit val conv: PositionedResult => (String, String, String, String) = { r =>
    (r.nextString(), r.nextString(), r.nextString(), r.nextString())
  }
}

class ControllerSpec extends AnyFlatSpec with MockFactory {}
