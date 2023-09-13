package com.rockthejvm.client

import cats.effect.*
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.*
import fs2.grpc.syntax.all.*
import com.rockthejvm.protos.orders.*
import fs2.Stream

object Client {
  private val managedChannelResource: Resource[IO, ManagedChannel] =
    NettyChannelBuilder
      .forAddress("127.0.0.1", 9999)
      .usePlaintext()
      .resource[IO]

  private def formatItemsToStr(items: Seq[Item]): Seq[String] = {
    items.map(x => s"[${x.qty} of ${x.name}]")
  }

  private def processor(
      orderStub: OrderFs2Grpc[IO, Metadata],
      orders: Stream[IO, OrderRequest]
  ): IO[List[String]] = {
    for {
      response <- orderStub.sendOrderStream(
        orders,
        new Metadata()
      )
      str <- Stream.eval(
        IO(
          s"Processed orderid: ${response.orderid} for items: ${formatItemsToStr(response.items)
              .mkString(" and ")}, totaling to ${response.total.toString}"
        )
      )
    } yield str
  }.compile.toList

  def runClient(orders: Stream[IO, OrderRequest]): IO[List[String]] =
    managedChannelResource
      .flatMap(ch => OrderFs2Grpc.stubResource[IO](ch))
      .use(orderResource => processor(orderResource, orders))
}
