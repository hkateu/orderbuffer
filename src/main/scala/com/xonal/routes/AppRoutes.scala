package com.xonal.routes

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import com.xonal.protos.orders.{OrderRequest, Item}
import org.http4s.circe.*
import io.circe.Decoder
import io.circe.syntax.*
import scala.util.Random
import com.xonal.client.Client.processorFn
import fs2.Stream
import squants.market.USD
import scala.math.BigDecimal

object MyRoutes {
  case class Orders(values: Seq[OrderRequest])
  given ordersEntityDecoder: EntityDecoder[IO, Orders] = jsonOf[IO, Orders]

  given ordersDecoder: Decoder[Orders] = Decoder.instance { h =>
    h.get[Seq[OrderRequest]]("orders").map { orders =>
      Orders(orders)
    }
  }

  given itemDecoder: Decoder[Item] = Decoder.instance { h =>
    for
      name <- h.get[String]("name")
      qty <- h.get[Int]("quantity")
      amount <- h.get[BigDecimal]("amount")
    yield Item.of(
      name,
      qty,
      USD(amount)
    ) // Item().withName(name).withQty(qty).withAmount(USD(amount))
  }

  given orDecoder: Decoder[OrderRequest] = Decoder.instance { h =>
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

  def restService = HttpRoutes.of[IO] {
    case req @ GET -> Root / "index.html" =>
      StaticFile
        .fromString[IO](
          "src/main/scala/index.html",
          Some(req)
        )
        .getOrElseF(NotFound())
    case req @ POST -> Root / "submit" =>
      req
        .as[Orders]
        .flatMap { x =>
          processorFn(Stream.emits(x.values).covary[IO])
        }
        .handleError(x => List(x.getMessage))
        .flatMap(x => Ok(x.asJson))
  }
}
