package com.xonal.protos

import squants.market.{USD, Money}
import scalapb.TypeMapper

given typeMapper: TypeMapper[Double, Money] =
  TypeMapper[Double, Money](s => USD(s))(_.amount.toDouble)
