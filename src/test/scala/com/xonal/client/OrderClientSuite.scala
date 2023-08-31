package com.xonal.client

import weaver.*
import cats.effect.*
import com.xonal.protos.orders.{Item, OrderRequest}
import com.xonal.client.Client.*
import fs2.Stream

object OrderClientSuite extends SimpleIOSuite {

  val myItems: Seq[Item] = Seq(
    Item.of("Iphone", 2, 1000),
    Item.of("Motorolla", 1, 900)
  )

  val sampleOrder = OrderRequest(
    1000,
    myItems
  )

  pureTest(
    "formatItemsToStr should produce Seq string in [qty of name] format"
  ) {
    expect.eql(
      Seq("[2 of Iphone]", "[1 of Motorolla]"),
      formatItemsToStr(myItems)
    )
  }

  pureTest("processorFn should respond with IO[List[String]") {
    expect(processorFn(Stream.emit(sampleOrder)).isInstanceOf[IO[List[String]]])
  }
}
