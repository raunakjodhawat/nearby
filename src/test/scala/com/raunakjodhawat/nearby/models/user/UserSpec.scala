package com.raunakjodhawat.nearby.models.user

import java.util.Date

import zio.test._
import zio.test.junit.JUnitRunnableSpec

object UserSpec {
  val dateLong: Long = 1690788936
  val date: Date = new Date(dateLong)
  val user: User = User(
    Some(1L),
    "username",
    "user123",
    Some("secret"),
    "email",
    Some("phone"),
    Some("address"),
    Some("city"),
    Some("state"),
    Some("country"),
    Some("zip"),
    Some(UserLocation(2.3, 4.5)),
    Some(date),
    Some(date),
    Some(UserStatus("ACTIVE")),
    Some(UserLoginStatus("LOGGED_IN")),
    Some(Avatar("AV_1"))
  )
}
class UserSpec extends JUnitRunnableSpec {
  import UserSpec._
  def spec = suite("Users")(
    test("UserStatus") {
      assert(UserStatus("Active").toString)(Assertion.equalTo("UserStatus(Active)"))
    },
    test("UserLoginStatus") {
      assert(UserStatus("LOGGED_IN").toString)(Assertion.equalTo("UserStatus(LOGGED_IN)"))
    },
    test("Avatar") {
      assert(Avatar("AV_1").toString)(Assertion.equalTo("Avatar(AV_1)"))
    },
    test("UserLocation") {
      assert(UserLocation(12, 10).toString)(Assertion.equalTo("UserLocation(12.0,10.0)"))
    },
    test("User") {
      assert(user.toString)(
        Assertion.equalTo(
          "User(Some(1),username,user123,Some(secret),email,Some(phone),Some(address),Some(city),Some(state),Some(country),Some(zip),Some(UserLocation(2.3,4.5)),Some(Tue Jan 20 19:09:48 IST 1970),Some(Tue Jan 20 19:09:48 IST 1970),Some(UserStatus(ACTIVE)),Some(UserLoginStatus(LOGGED_IN)),Some(Avatar(AV_1)))"
        )
      )
    }
  )
}
