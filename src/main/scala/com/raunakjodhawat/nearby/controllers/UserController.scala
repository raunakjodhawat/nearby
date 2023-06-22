package com.raunakjodhawat.nearby.controllers

import com.raunakjodhawat.nearby.models.user.Avatar.Avatar
import com.raunakjodhawat.nearby.models.user.UserLoginStatus.UserLoginStatus
import com.raunakjodhawat.nearby.models.user.UserStatus.UserStatus
import com.raunakjodhawat.nearby.models.user.{
  Avatar,
  User,
  UserAlreadyExistsException,
  UserLocation,
  UserLoginStatus,
  UserStatus
}
import com.raunakjodhawat.nearby.repository.user.UserRepository
import slick.jdbc.PostgresProfile
import zio.{Task, ZIO}
import zio.http._
import zio.json._
import zio.json.internal.{RetractReader, Write}
import zio._

import java.text.SimpleDateFormat
import java.util.Date
import scala.Long
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object UserController {
  private val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit val dateDecoder: JsonDecoder[Date] = new JsonDecoder[Date] {
    override def unsafeDecode(trace: List[JsonError], in: RetractReader): Date = {
      val date: String = in.toString
      df.parse(date)
    }
  }
  implicit val dateEncoder: JsonEncoder[Date] = new JsonEncoder[Date] {
    override def unsafeEncode(a: Date, indent: Option[Int], out: Write): Unit = out.write(df.format(a))
  }

  implicit val userLocationEncoder: JsonEncoder[UserLocation] = new JsonEncoder[UserLocation] {
    override def unsafeEncode(a: UserLocation, indent: Option[Int], out: Write): Unit =
      out.write(s"""{"latitude":${a.lat},"longitude":${a.long}}""")
  }
  implicit val userLocationDecoder: JsonDecoder[UserLocation] = new JsonDecoder[UserLocation] {
    override def unsafeDecode(trace: List[JsonError], in: RetractReader): UserLocation = {
      val json = in.toString
      val latitude = json.substring(json.indexOf(":") + 1, json.indexOf(",")).toDouble
      val longitude = json.substring(json.lastIndexOf(":") + 1, json.indexOf("}")).toDouble
      UserLocation(latitude, longitude)
    }
  }

  implicit val loginStatusEncoder: JsonEncoder[UserLoginStatus] = new JsonEncoder[UserLoginStatus] {
    override def unsafeEncode(a: UserLoginStatus, indent: Option[Int], out: Write): Unit =
      out.write(s""""${a.toString}"""")
  }
  implicit val loginStatusDecoder: JsonDecoder[UserLoginStatus] = new JsonDecoder[UserLoginStatus] {
    override def unsafeDecode(trace: List[JsonError], in: RetractReader): UserLoginStatus = {
      val json = in.toString
      UserLoginStatus.withName(json.substring(1, json.length - 1))
    }
  }

  implicit val avatarEncoder: JsonEncoder[Avatar] = new JsonEncoder[Avatar] {
    override def unsafeEncode(a: Avatar, indent: Option[Int], out: Write): Unit = out.write(s""""${a.toString}"""")
  }

  implicit val avatarDecoder: JsonDecoder[Avatar] = new JsonDecoder[Avatar] {
    override def unsafeDecode(trace: List[JsonError], in: RetractReader): Avatar = {
      val json = in.toString
      Avatar.withName(json.substring(1, json.length - 1))
    }
  }
  implicit val userStatusEncoder: JsonEncoder[UserStatus] = new JsonEncoder[UserStatus] {
    override def unsafeEncode(a: UserStatus, indent: Option[Int], out: Write): Unit =
      out.write(s""""${a.toString}"""")
  }
  implicit val userStatusDecoder: JsonDecoder[UserStatus] = new JsonDecoder[UserStatus] {
    override def unsafeDecode(trace: List[JsonError], in: RetractReader): UserStatus = {
      val json = in.toString
      UserStatus.withName(json.substring(1, json.length - 1))
    }
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
    case Method.GET -> api_path =>
      getAllUsers()
    case req @ Method.POST -> api_path =>
      createUser(req.body)
    case req @ Method.POST -> api_path / id =>
      updateUser(req.body)

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

  def createUser(body: Body): ZIO[Any, Throwable, Response] = {
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

  def updateUser(body: Body): ZIO[Any, Throwable, Response] = {
    body.asString
      .map(x => {
        x.fromJson[User].map { user =>
          {
            user_repository.updateUser(user).map(x => Response.text(x.toString)).orDie
          }
        } match {
          case Left(e)      => Response.text(e)
          case Right(value) => Response.text(value.toString)
        }
      })
  }
}
