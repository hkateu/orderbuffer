package com.xonal.client

import cats.effect.*
import _root_.io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import _root_.io.grpc.*
import fs2.grpc.syntax.all.*
import com.xonal.protos.orders.*
import fs2.Stream

object Client {
  val managedChannelResource: Resource[IO, ManagedChannel] =
    NettyChannelBuilder
      .forAddress("127.0.0.1", 9999)
      .usePlaintext()
      .resource[IO]

  def formatItemsToStr(items: Seq[Item]): Seq[String] = {
    items.map(x => s"[${x.qty} of ${x.name}]")
  }

  def processorFn(orders: Stream[IO, OrderRequest]): IO[List[String]] = {
    def processor(orderStub: OrderFs2Grpc[IO, Metadata]): IO[List[String]] = {
      for {
        response <- orderStub.sendOrderStream(orders, new Metadata())
        str <- Stream.eval(
          IO(
            s"Processed orderid: ${response.orderid} for items: ${formatItemsToStr(response.items)
                .mkString(" and ")}, totaling to ${response.total.toString}"
          )
        )
      } yield str
    }.compile.toList

    def runClient: IO[List[String]] =
      managedChannelResource
        .flatMap(ch => OrderFs2Grpc.stubResource[IO](ch))
        .use(processor)

    runClient
  }
}
