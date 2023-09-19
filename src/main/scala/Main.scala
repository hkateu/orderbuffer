import cats.effect.*
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import com.rockthejvm.routes.AppRoutes.restService
import com.rockthejvm.service.Server.grpcServer
import cats.syntax.parallel.*

object Main extends IOApp {
  def httpServerStream =
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(restService.orNotFound)
      .build
      .use(_ => IO.never)

  def run(args: List[String]): IO[ExitCode] =
    (
      httpServerStream,
      grpcServer
        .evalMap(svr => IO(svr.start()))
        .useForever
    )
      .parMapN((http, grpc) => ())
      .as(ExitCode.Success)
}
