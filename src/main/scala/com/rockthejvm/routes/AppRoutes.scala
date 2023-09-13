package com.rockthejvm.routes

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import com.rockthejvm.protos.orders.{OrderRequest, Item}
import org.http4s.circe.*
import io.circe.Decoder
import io.circe.syntax.*
import scala.util.Random
import com.rockthejvm.client.Client
import fs2.Stream
import squants.market.USD
import scala.io.Source

object AppRoutes {
  case class Orders(values: Seq[OrderRequest])

  object Orders {
    private given itemDecoder: Decoder[Item] = Decoder.instance { h =>
      for
        name <- h.get[String]("name")
        qty <- h.get[Int]("quantity")
        amount <- h.get[Double]("amount")
      yield Item.of(
        name,
        qty,
        USD(amount)
      ) // Item().withName(name).withQty(qty).withAmount(USD(amount))
    }

    private given orDecoder: Decoder[OrderRequest] = Decoder.instance { h =>
      h.get[Seq[Item]]("items").map { items =>
        OrderRequest.of(Random.between(1000, 2000), items)

        // OrderRequest()
        //  .withOrderid(Random.between(1000,2000))
        //  .withItems(items)

        // OrderRequest(
        //   Random.between(1000,2000),
        //   items,
        //   _root_.scalapb.UnknownFieldSet.empty
        // )
      }
    }

    given ordersDecoder: Decoder[Orders] = Decoder.instance { h =>
      h.get[Seq[OrderRequest]]("orders").map { orders =>
        Orders(orders)
      }
    }

    given ordersEntityDecoder: EntityDecoder[IO, Orders] = jsonOf[IO, Orders]
  }

  def restService = HttpRoutes.of[IO] {
    case req @ GET -> Root / "index.html" =>
      StaticFile
        .fromString[IO](
          "src/main/resources/index.html",
          Some(req)
        )
        .getOrElseF(NotFound())
    case req @ POST -> Root / "submit" =>
      req
        .as[Orders]
        .flatMap { x =>
          Client.runClient(Stream.emits(x.values).covary[IO])
        }
        .handleError(x => List(x.getMessage))
        .flatMap(x => Ok(x.asJson))
  }
}
