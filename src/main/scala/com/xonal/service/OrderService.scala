package com.xonal.service

import com.xonal.protos.orders.*
import cats.effect.*
import _root_.io.grpc.*
import fs2.Stream
import _root_.io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
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

  private def runServer(service: ServerServiceDefinition) = NettyServerBuilder
    .forPort(9999)
    .addService(service)
    .resource[IO]
    .evalMap(server => IO(server.start()))
    .useForever

  val grpcServer = orderService.use(srvr => runServer(srvr))
}
