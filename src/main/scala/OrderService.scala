package com.rockthejvm.service

import com.rockthejvm.protos.orders.*
import cats.effect.*
import io.grpc.*
import fs2.Stream
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all.*

class OrderService extends OrderFs2Grpc[IO, Metadata] {
  override def sendOrderStream(
      request: Stream[IO, OrderRequest],
      ctx: Metadata
  ): Stream[IO, OrderReply] = {
    request.map { orderReq =>
      OrderReply(
        orderReq.orderid,
        orderReq.items,
        orderReq.items.map(i => i.amount).reduce(_ + _)
      )
    }
  }
}

object Server {
  private val orderService: Resource[IO, ServerServiceDefinition] =
    OrderFs2Grpc.bindServiceResource[IO](new OrderService)

  private def runServer(
      service: ServerServiceDefinition
  ): Resource[IO, Server] =
    NettyServerBuilder
      .forPort(9999)
      .addService(service)
      .resource[IO]

  val grpcServer: Resource[IO, Server] =
    orderService
      .flatMap(x => runServer(x))

}
