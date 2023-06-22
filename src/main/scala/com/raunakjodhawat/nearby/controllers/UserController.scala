package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.models.user.Avatar.Avatar
import com.raunakjodhawat.nearby.models.user.UserLoginStatus.UserLoginStatus
import com.raunakjodhawat.nearby.models.user.UserStatus.UserStatus
import com.raunakjodhawat.nearby.models.user.{Avatar, User, UserLocation, UserLoginStatus, UserStatus}
import com.raunakjodhawat.nearby.repository.user.UserRepository
import slick.jdbc.PostgresProfile
import zio._
import zio.http._
import zio.json._
import zio.json.internal.{RetractReader, Write}

import java.text.SimpleDateFormat
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global

object UserController {
  private val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dateDecoder: JsonDecoder[Date] = (trace: List[JsonError], in: RetractReader) => {
    val date: String = in.toString
    df.parse(date)
  }
  implicit val dateEncoder: JsonEncoder[Date] = (a: Date, indent: Option[Int], out: Write) => out.write(df.format(a))

  implicit val userLocationEncoder: JsonEncoder[UserLocation] = (a: UserLocation, indent: Option[Int], out: Write) => out.write(s"""{"latitude":${a.lat},"longitude":${a.long}}""")
  implicit val userLocationDecoder: JsonDecoder[UserLocation] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    val latitude = json.substring(json.indexOf(":") + 1, json.indexOf(",")).toDouble
    val longitude = json.substring(json.lastIndexOf(":") + 1, json.indexOf("}")).toDouble
    UserLocation(latitude, longitude)
  }

  implicit val loginStatusEncoder: JsonEncoder[UserLoginStatus] = (a: UserLoginStatus, indent: Option[Int], out: Write) => out.write(s""""${a.toString}"""")
  implicit val loginStatusDecoder: JsonDecoder[UserLoginStatus] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    UserLoginStatus.withName(json.substring(1, json.length - 1))
  }

  implicit val avatarEncoder: JsonEncoder[Avatar] = (a: Avatar, indent: Option[Int], out: Write) => out.write(s""""${a.toString}"""")

  implicit val avatarDecoder: JsonDecoder[Avatar] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    Avatar.withName(json.substring(1, json.length - 1))
  }
  implicit val userStatusEncoder: JsonEncoder[UserStatus] = (a: UserStatus, indent: Option[Int], out: Write) => out.write(s""""${a.toString}"""")
  implicit val userStatusDecoder: JsonDecoder[UserStatus] = (trace: List[JsonError], in: RetractReader) => {
    val json = in.toString
    UserStatus.withName(json.substring(1, json.length - 1))
  }
  implicit val userDecoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  implicit val userEncoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
}
class UserController(basePath: Path, db: PostgresProfile.backend.Database) {
  import UserController._
  private val api_path = basePath / "user"
  private val user_repository = new UserRepository(db)
  val api_route = Http.collectZIO[Request] {
    case Method.GET -> api_path / long(id) =>
      getUserById(id)
    case req @ Method.POST -> api_path / long(id) =>
      updateUser(req.body, id)
    case Method.GET -> api_path =>
      getAllUsers()
    case req @ Method.POST -> api_path =>
      createUser(req.body)
  }

  private def getAllUsers(): ZIO[Any, Throwable, Response] = {
    user_repository
      .getAllUsers()
      .join
      .map(x => Response.json(x.toJson))
  }

  private def getUserById(id: Long): ZIO[Any, Throwable, Response] = {
    user_repository
      .getUserById(id)
      .join
      .map(x => Response.json(x.toJson))
  }

  private def createUser(body: Body): ZIO[Any, Throwable, Response] = {
    body.asString
      .map(_.fromJson[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) =>
          user_repository
            .createUser(user)
            .join
            .map(x => if (x == 1) Response.status(Status.Created) else Response.status(Status.BadRequest))
      }
  }

  private def updateUser(body: Body, id: Long): ZIO[Any, Throwable, Response] = {
    body.asString
      .map(_.fromJson[User])
      .flatMap {
        case Left(e) => ZIO.fail(new Exception(e))
        case Right(user) =>
          user_repository
            .updateUser(user, id)
            .join
            .map(user => Response.json(user.toJson))
      }
  }
}
