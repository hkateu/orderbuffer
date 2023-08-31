package com.xonal.service

import weaver.*
import cats.effect.*
import fs2.Stream
import io.grpc.Metadata
import com.xonal.SampleData.{myItems, sampleOrder}

object OrderServiceSuite extends SimpleIOSuite {
  val osInstance = new OrderService
  pureTest("sendOrderStream must be of type Stream[OrderReply]") {
    expect(
      osInstance
        .sendOrderStream(Stream.emit(sampleOrder), new Metadata())
        .isInstanceOf[Stream[IO, OrderReply]]
    )
  }
}
