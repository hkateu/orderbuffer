package com.xonal.client

import weaver.*
import cats.effect.IO
import cats.effect.kernel.Resource

// object ExpectationsSuite extends SimpleIOSuite {
//   object A {
//     object B {
//       object C {
//         def test(a: Int) = a + 5
//       }
//     }
//   }

//   pureTest("Simple expectations (success)") {
//     val z = 15
//     expect(A.B.C.test(z) == z + 5)
//   }

//   // pureTest("Simple expectations (Failure)"){
//   //     val z = 15
//   //     expect(A.B.C.test(z) % 7 == 0)
//   // }

//   pureTest("And/Or composition (success)") {
//     expect(1 != 2) and expect(2 != 1) or expect(2 != 3)
//   }

//   // pureTest("And/Or composition (failure)"){
//   //     (expect(1 != 2) and expect(2 == 1)) or (expect(2 == 3))
//   // }

//   pureTest("Vargargs composition (success)") {
//     expect.all(1 + 1 == 2, 2 + 2 == 4, 4 * 2 == 8)
//   }

//   // pureTest("Vargargs composition (failure)"){
//   //     expect.all(1 + 1 == 2, 2 + 2 == 5, 4 * 2 == 8)
//   // }

//   pureTest("Working with collections (success)") {
//     forEach(List(1, 2, 3))(i =>
//       expect(i < 5) and
//         forEach(Option("hello"))(msg => expect.same("hello", msg)) and
//         exists(List("a", "b", "c"))(i => expect(i == "c")) and
//         exists(Vector(true, true, false))(i => expect(i == false))
//     )
//   }

//   // pureTest("Working with collections (failure 1)"){
//   //     forEach(Vector("hello", "world"))(msg => expect.same("hello", msg))
//   // }

//   // pureTest("Working with collections (failure 2)"){
//   //     forEach(Option(39))(i => expect(i > 50))
//   // }

//   import cats.Eq
//   case class Test(d: Double)
//   given eqTest: Eq[Test] = Eq.by[Test, Double](_.d)

//   pureTest("String equality (success)") {
//     expect.eql("hello", "hello") and
//       expect.eql(List(1, 2, 3), List(1, 2, 3)) and
//       expect.eql(Test(25.0), Test(25.0))
//   }

//   // pureTest("Strict equality (falure 1)"){
//   //     expect.eql("hello", "world")
//   // }

//   // pureTest("Strict equality (falure 2)"){
//   //     expect.eql(List(1,2,3), List(1,19,3))
//   // }

//   // pureTest("Strict equality (falure 3)"){
//   //     expect.eql(Test(25.0), Test(50.0))
//   // }

//   class Hello(val d: Double) {
//     override def toString(): String = s"Hello to $d"
//     override def equals(other: Any): Boolean =
//       if (other != null && other.isInstanceOf[Hello])
//         other.asInstanceOf[Hello].d == this.d
//       else
//         false
//   }

//   pureTest("Relaxed equality comparison (success)") {
//     expect.same(new Hello(25.0), new Hello(25.0))
//   }

//   // pureTest("Relaxed equality comparison (failure)"){
//   //     expect.same(new Hello(25.0), new Hello(50.0))
//   // }

//   // pureTest("Non macro-based expectations"){
//   //     val condition: Boolean = false
//   //     if(condition) success else failure("Condition failed")
//   // }

//   // test("Failing fast expectations"){
//   //     for{
//   //         h <- IO.pure("helo")
//   //         _ <- expect(h.isEmpty).failFast
//   //     } yield success
//   // }
// }

// object FirstSuite extends SimpleIOSuite {
//   val randomUUID = IO(java.util.UUID.randomUUID())
//   test("hello side-efects") {
//     for {
//       x <- randomUUID
//       y <- randomUUID
//     } yield expect(x != y)
//   }

//   test("test other expectations") {
//     val res: Either[String, Int] = Right(4)
//     val result = if (5 == 5) success else failure("oh no")
//     IO(expect(5 == 5) || expect(8 == 8) && expect(4 == 4))
//     IO(expect.all(5 == 5, 2 == 2, 3 > 2))
//     IO(forEach(List(1, 2, 3))(i => expect(i < 5)))
//     IO(exists(Option(5))(n => expect(n > 3)))
//     IO(matches(Option(4)) { case Some(x) => expect.eql(4, x) })
//     IO(whenSuccess(res)(n => expect.eql(4, n)))
//     IO(expect.eql(List(1, 2, 3), (1 to 3).toList))
//     IO(expect.same(List(1, 2, 3), (1 to 3).toList))

//     // for {
//     //   x <- IO("hello")
//     //   _ <- expect(x.length == 4).failFast
//     //   y = x + "bla"
//     //   _ <- expect(y.size > x.size).failFast
//     // } yield expect(y.contains(x))

//   }

// }

// object SharedResources extends GlobalResource {
//   def sharedResources(global: GlobalWrite): Resource[cats.effect.IO, Unit] =
//     for {
//       foo <- Resource.pure[IO, String]("hello world!")
//       _ <- global.putR(foo)
//     } yield ()
// }

// class SharingSuite(global: GlobalRead) extends IOSuite {
//   type Res = String
//   def sharedResource: Resource[cats.effect.IO, Res] =
//     global.getOrFailR[String]()

//   test("a stranger, from the outside !  ooooh") { sharedString =>
//     IO(expect(sharedString == "hello world!"))
//   }
// }

// class OhterSharingSuite(global: GlobalRead) extends IOSuite {
//   type Res = Option[Int]

//   def sharedResource: Resource[IO, Option[Int]] =
//     global.getR[Int]()

//   test("oops, forgot somehing here") { sharedInt =>
//     IO(expect(sharedInt.isEmpty))
//   }
// }

// object MyResources extends GlobalResource {
//   override def sharedResources(
//       global: GlobalWrite
//   ): Resource[cats.effect.IO, Unit] =
//     baseResources.flatMap(global.putR(_))

//   def baseResources: Resource[IO, String] =
//     Resource.pure[IO, String]("hello world!")

//   def sharedResourceOrFallback(read: GlobalRead): Resource[IO, String] =
//     read.getR[String]().flatMap {
//       case Some(value) => Resource.eval(IO(value))
//       case None        => baseResources
//     }
// }

// class MySuite(global: GlobalRead) extends IOSuite {
//   import MyResources.*

//   override type Res = String

//   override def sharedResource: Resource[cats.effect.IO, Res] =
//     sharedResourceOrFallback(global)

//   test("a stranger, from the outside ! ohhhh") { sharedString =>
//     IO(expect(sharedString == "hello world!"))
//   }
// }

// class MyOtherSuite(global: GlobalRead) extends IOSuite {
//   import MyResources.*

//   override type Res = String

//   def sharedResource: Resource[IO, String] = sharedResourceOrFallback(global)

//   test("oops, forgot something here") { sharedString =>
//     IO(expect(sharedString == "hello world"))
//   }
// }

object LoggedTests extends IOSuite {
  loggedTest("just logging some stuff") { log =>
    for {
      _ <- log.info("opsie daisy")
    } yield expect(2 + 2 == 5)
  }

  override type Res = String

  override def sharedResource: Resource[IO, Res] =
    Resource.pure[IO, Res]("hello")

  test("God requests lead to good results") { (sharedString, log) =>
    for {
      _ <- log.info(sharedString)
    } yield expect(2 + 2 == 4)
  }
}
