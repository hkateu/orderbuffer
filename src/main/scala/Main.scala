import cats.effect.*
import fs2.Stream
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import com.xonal.routes.MyRoutes.restService
import com.xonal.service.Server.grpcServer

object Main extends IOApp {
  def httpServerStream = Stream.eval(
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(restService.orNotFound)
      .build
      .use(_ => IO.never)
  )

  val grpcServerStream = Stream.eval(grpcServer)

  def run(args: List[String]): IO[ExitCode] = grpcServerStream
    .concurrently(httpServerStream)
    .compile
    .toList
    .as(ExitCode.Success)
}
