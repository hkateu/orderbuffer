package com.rockthejvm.routes

import weaver.*
import cats.effect.*
import org.http4s.client.Client
import org.http4s.*
import com.comcast.ip4s.*

object AppRoutesSuite extends IOSuite {
  test("Requests to restService should return 200 status code") {
    val request: Request[IO] =
      Request(
        method = Method.POST,
        uri = uri"localhost:8080/submit"
      )
  }
}
