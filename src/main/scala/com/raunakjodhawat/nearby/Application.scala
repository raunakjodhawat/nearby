package com.raunakjodhawat.nearby

import zio.http._
import zio._

object Application extends ZIOAppDefault {
  private val app = Http.collect[Request] { case Method.GET -> Root / "ping" =>
    Response.text("pong")
  }
  override def run = Server.serve(app).provide(Server.default)
}
