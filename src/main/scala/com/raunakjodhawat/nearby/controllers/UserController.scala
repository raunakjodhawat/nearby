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
  println("reached here")
  private val api_path = basePath / "user"
  private val user_repository = new UserRepository(db)
  val api_route = Http.collectZIO[Request] {
    case Method.GET -> api_path =>
      getAllUsers()
    case req @ Method.POST -> api_path =>
      createUser(req.body)
    case req @ Method.POST -> api_path / id =>
      updateUser(req.body)
    case Method.GET -> api_path / id =>
      getUserById(id.toLong)

  }

  def getAllUsers(): ZIO[Any, Throwable, Response] = {
    user_repository.getAllUsers().map(x => Response.text(x.toString))
  }

  def createUser(body: Body): ZIO[Any, Throwable, Response] = {
    val payload: Task[String] = body.asString
    val mayBeUser: ZIO[Any, Throwable, Either[String, User]] = payload
      .map(_.fromJson[User])
    val createResponse: ZIO[Any, Throwable, Response] = mayBeUser
      .map(x => {
        val mayBeResponse: Either[String, Task[Int]] = x
          .map { user =>
            Runtime.default.unsafe(ZIO.fromFuture(implicit ec => user_repository.createUser(user)))
            ZIO.fromFuture(implicit ec => user_repository.createUser(user))
          }
        mayBeResponse match {
          case Left(e) => Response.text(e)
          case Right(value: Task[Int]) => {
            val runtime = Runtime.default

            Unsafe.unsafe { implicit unsafe =>
              runtime.unsafeRunToFuture(ZIO.fromFuture(ex => Future(1))).run(value).getOrThrowFiberFailure()
            }
          }
        }
      })
    createResponse
  }
  def getUserById(id: Long) = {
    println("get a user by id")
    user_repository
      .getUserById(id)
      .map { user =>
        {
          user match {
            case Some(user) => Response.text(user.toString)
            case None       => Response.text("User not found")
          }
        }
      }
      .orDie
  }

  def updateUser(body: Body) = {
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
