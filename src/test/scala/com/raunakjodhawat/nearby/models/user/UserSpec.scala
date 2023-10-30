package com.raunakjodhawat.nearby.models.user

import com.raunakjodhawat.nearby.testUser
import org.junit.runner.RunWith

import java.util.Date
import zio.test.*
import zio.test.junit.{JUnitRunnableSpec, ZTestJUnitRunner}

@RunWith(classOf[ZTestJUnitRunner])
class UserSpec extends JUnitRunnableSpec {
  def spec = suite("Users")(
    test("Avatar") {
      assert(Avatar.AV_1.name)(Assertion.equalTo("AV_1"))
    },
    test("UserLocation") {
      assert(UserLocation(12, 10).toString)(Assertion.equalTo("UserLocation(12.0,10.0)"))
    },
    test("User") {
      assert(testUser().toString)(
        Assertion.equalTo(
          "User(1,username,password,secret,Some(email),Some(name),Some(bio),Some(phone),Some(UserLocation(2.3,4.5)),Some(Tue Jan 20 19:09:48 IST 1970),Some(Tue Jan 20 19:09:48 IST 1970),false,Some(AV_1))"
        )
      )
    }
  )
}
