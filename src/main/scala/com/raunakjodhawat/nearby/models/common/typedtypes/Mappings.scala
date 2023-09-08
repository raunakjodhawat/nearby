package com.raunakjodhawat.nearby.models.common.typedtypes

import java.util.Date
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*

object Mappings {
  given dateMapping: TypedType[Date] = MappedColumnType.base[Date, String](
    e => e.toString,
    _ => new Date()
  )
}
