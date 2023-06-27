package com.raunakjodhawat.nearby.utils

import java.util.UUID.randomUUID
object Utils {
  def secretKey(): String = randomUUID.toString
}
