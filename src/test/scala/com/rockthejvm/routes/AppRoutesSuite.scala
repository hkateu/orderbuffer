package com.rockthejvm.routes

import weaver.*
import org.http4s.client.Client
import cats.effect.*
import com.rockthejvm.routes.AppRoutes.restService
import com.rockthejvm.client.Client.runClient
import com.rockthejvm.protos.orders.*
import squants.market.USD
import fs2.Stream
import io.grpc.netty.shaded.io.netty.channel.embedded.EmbeddedChannel

object AppRoutesSuite extends SimpleIOSuite {
  test("/index.html should return proper status code") {
    Client
      .fromHttpApp[IO](restService.orNotFound)
      .statusFromString("/index.html")
      .map(status => expect(status.code == 200))
  }

  // test("grpc server should return IO[List[String]]") {
  //   runClient(
  //     Stream(
  //       OrderRequest.of(1000, Seq(Item.of("Iphone", 1, USD(999.99))))
  //     )
  //   ).map(v => expect(v.isInstanceOf[List[String]]))
  // }
}
