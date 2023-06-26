package com.raunakjodhawat.nearby.models.user

import org.scalatest.flatspec.AnyFlatSpec

class UserSpec extends AnyFlatSpec {
  "user Status" should "be able to use all values" in {
    assert(UserStatus.ACTIVE.toString == "ACTIVE")
    assert(UserStatus.INACTIVE.toString == "INACTIVE")
    assert(UserStatus.DELETED.toString == "DELETED")
  }

  "user login status" should "be able to use all values" in {
    assert(UserLoginStatus.LOGGED_IN.toString == "LOGGED_IN")
    assert(UserLoginStatus.LOGGED_OUT.toString == "LOGGED_OUT")
  }

  "avatar" should "be able to use all values" in {
    assert(Avatar.AV_1.toString == "AV_1")
    assert(Avatar.AV_2.toString == "AV_2")
    assert(Avatar.AV_3.toString == "AV_3")
    assert(Avatar.AV_4.toString == "AV_4")
    assert(Avatar.AV_5.toString == "AV_5")
    assert(Avatar.AV_6.toString == "AV_6")
    assert(Avatar.AV_7.toString == "AV_7")
    assert(Avatar.AV_8.toString == "AV_8")
    assert(Avatar.AV_9.toString == "AV_9")
    assert(Avatar.AV_10.toString == "AV_10")
  }

  "user location" should "be able to use all values" in {
    val userLocation = UserLocation(1.0, 1.0)
    assert(userLocation.lat == 1.0)
    assert(userLocation.long == 1.0)
  }

  "user" should "be valid" in {
    val user = User(
      id = Some(1),
      username = "username",
      password = "password",
      salt = "salt",
      email = "email",
      phone = "phone",
      address = Some("address"),
      city = Some("city"),
      state = Some("state"),
      country = Some("country"),
      pincode = Some("pincode"),
      location = Some(UserLocation(1.0, 1.0)),
      created_at = Some(new java.util.Date()),
      updated_at = Some(new java.util.Date()),
      status = Some(UserStatus.ACTIVE),
      login_status = Some(UserLoginStatus.LOGGED_IN),
      avatar = Some(Avatar.AV_1)
    )
    assert(user.id.get == 1)
    assert(user.username == "username")
    assert(user.password == "password")
    assert(user.salt == "salt")
    assert(user.email == "email")
    assert(user.phone == "phone")
    assert(user.address.get == "address")
    assert(user.city.get == "city")
    assert(user.state.get == "state")
    assert(user.country.get == "country")
    assert(user.pincode.get == "pincode")
    assert(user.location.get.lat == 1.0)
    assert(user.location.get.long == 1.0)
    assert(user.created_at.get != null)
    assert(user.updated_at.get != null)
    assert(user.status.get == UserStatus.ACTIVE)
    assert(user.login_status.get == UserLoginStatus.LOGGED_IN)
    assert(user.avatar.get == Avatar.AV_1)
  }
}
