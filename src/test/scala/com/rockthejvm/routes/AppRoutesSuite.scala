package com.rockthejvm.routes

import weaver.*
import org.http4s.client.Client
import cats.effect.*
import com.rockthejvm.routes.AppRoutes.restService
import com.rockthejvm.service.Server.grpcServer
import fs2.Stream
import scala.concurrent.duration.*
import com.rockthejvm.client.Client.runClient
import com.rockthejvm.protos.orders.*
import squants.market.USD

object AppRoutesSuite extends SimpleIOSuite {
  test("/index.html should return proper status code") {
    Client
      .fromHttpApp[IO](restService.orNotFound)
      .statusFromString("/index.html")
      .map(status => expect(status.code == 200))
  }

  test("grpc server should return IO[List[String]]") {
    val server = grpcServer.flatMap { svr =>
      Resource.make(IO(svr.start()))(svr => IO(svr.shutdown()) *> IO.unit)
    }

    Stream(server.use(s => IO(s.start()) >> IO.unit))
      .concurrently(
        Stream.sleep(5.millis) ++
          Stream(
            runClient(
              Stream(
                OrderRequest.of(1000, Seq(Item.of("Iphone", 1, USD(999.99))))
              )
            )
          )
      )
      .compile
      .last
      .map(v => expect(v.get.isInstanceOf[IO[List[String]]]))
  }
}
