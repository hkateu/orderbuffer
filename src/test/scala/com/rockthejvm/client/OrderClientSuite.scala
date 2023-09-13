package com.rockthejvm.client

import weaver.*
import cats.effect.*
import com.rockthejvm.protos.orders.{Item, OrderRequest}
import com.rockthejvm.client.Client.*
import fs2.Stream
import squants.market.USD

object OrderClientSuite extends SimpleIOSuite {

  val myItems: Seq[Item] = Seq(
    Item.of("Iphone", 2, USD(999.99)),
    Item.of("Motorolla", 1, USD(900.50))
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
