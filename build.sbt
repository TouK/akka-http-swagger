name := "akka-http-swagger"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature")

resolvers ++= Seq(
"hseeberger at bintray" at "http://dl.bintray.com/hseeberger/maven"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "io.swagger" % "swagger-core" % "1.5.0",
  "io.swagger" %% "swagger-scala-module" % "1.0.0",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.0.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0",
  "org.scalatest" %% "scalatest" % "latest.integration" % "test"
)
