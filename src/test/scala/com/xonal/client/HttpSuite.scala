package com.xonal.client

import weaver.*
import cats.effect.*
import org.http4s.ember.client.*
import org.http4s.client.*
import java.util.concurrent.ConcurrentLinkedQueue

// object HttpSuite extends IOSuite {
//   // sharing a single http client across all tests

//   override type Res = Client[IO]
//   override def sharedResource: Resource[cats.effect.IO, Res] =
//     EmberClientBuilder.default[IO].build

//   test("good requests lead to good results") { httpClient =>
//     for {
//       statusCode <- httpClient.get("https://httpbin.org/get") { response =>
//         IO.pure(response.status.code)
//       }
//     } yield expect(statusCode == 200)
//   }

//   test("bad requests lead to bad results") { httpClient =>
//     for {
//       statusCode <- httpClient.get("https://httpbin.org/oops") { response =>
//         IO.pure(response.status.code)
//       }
//     } yield expect(statusCode == 404)
//   }
// }

val order = new ConcurrentLinkedQueue[String]()

object ResourceDemo extends IOSuite {
  def record(msg: String) = IO(order.add(msg)).void

  override type Res = Int
  override def sharedResource: Resource[cats.effect.IO, Res] = {
    val acquire = record("Acquiring resource") *> IO.pure(42)
    val release = (i: Int) => record(s"Releasing resource $i")
    Resource.make(acquire)(release)
  }

  test("Test 1") { res =>
    record(s"Test 1 is using resource $res").as(success)
  }

  // test("Test 2") { res =>
  //   record(s"Test 2 is using resource $res").as(expect(res == 45))
  // }
}
