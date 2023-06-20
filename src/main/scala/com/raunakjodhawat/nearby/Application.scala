package com.raunakjodhawat.nearby

import zio.Console.*
import zio.*
import zio.http.{HttpApp, *}

object Application extends ZIOAppDefault {
  private val app = Http.collect[Request] { case Method.GET -> Root / "ping" =>
    Response.text("pong")
  }
  override def run = Server.serve(app).provide(Server.default)
}
