val scala3Version = "3.3.0"
val http4sVersion = "0.23.23"
val circeVersion = "0.14.5"
val squantsVersion = "1.8.3"

lazy val protobuf =
  project
    .in(file("protobuf"))
    .settings(
      name := "protobuf",
      scalaVersion := scala3Version,
      libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
      libraryDependencies += "org.typelevel" %% "squants" % squantsVersion
    )
    .enablePlugins(Fs2Grpc)

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "root",
      scalaVersion := scala3Version,
      libraryDependencies ++= Seq(
        "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion,
        "org.http4s" %% "http4s-ember-server" % http4sVersion,
        "org.http4s" %% "http4s-dsl" % http4sVersion,
        "org.http4s" %% "http4s-circe" % http4sVersion,
        "com.disneystreaming" %% "weaver-cats" % "0.8.3" % Test,
      ),
      testFrameworks += new TestFramework("weaver.framework.CatsEffect")
    )
    .dependsOn(protobuf)

scalafmtOnCompile := true
