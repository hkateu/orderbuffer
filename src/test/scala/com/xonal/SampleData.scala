package com.xonal

import com.xonal.protos.orders.{Item, OrderRequest}

object SampleData {
  val myItems: Seq[Item] = Seq(
    Item.of("Iphone", 2, 1000),
    Item.of("Motorolla", 1, 900)
  )

  val sampleOrder = OrderRequest(
    1000,
    myItems
  )
}
