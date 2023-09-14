package com.raunakjodhawat.nearby.models.common.typedtypes

import java.util.Date
import java.text.SimpleDateFormat
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*

object Mappings {
  private val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def dateToString(d: Date): String = sdf.format(d)
  def stringToDate(s: String): Date = sdf.parse(s)

  def dateToLong(d: Date): Long = d.getTime

  val break = "<--break-->"
  given dateMapping: TypedType[Date] = MappedColumnType.base[Date, String](
    date => sdf.format(date),
    dateInString => sdf.parse(dateInString)
  )
}
