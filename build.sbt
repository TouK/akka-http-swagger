import com.banno.license.Licenses._
import com.banno.license.Plugin.LicenseKeys._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import FilterKeys._

val commonSettings =
  filterSettings ++
    licenseSettings ++
    Seq(
      organization  := "pl.touk",
      scalaVersion  := "2.11.8",
      scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
      license := apache2("Copyright 2015 the original author or authors."),
      licenses :=  Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
      homepage := Some(url("https://github.com/TouK/akka-http-swagger")),
      removeExistingHeaderBlock := true,
      resolvers ++= Seq(
        "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
      )
    )

val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomExtra in Global := {
    <scm>
      <connection>scm:git:github.com/TouK/akka-http-swagger.git</connection>
      <developerConnection>scm:git:git@github.com/TouK/akka-http-swagger.git</developerConnection>
      <url>github.com/TouK/akka-http-swagger</url>
    </scm>
      <developers>
        <developer>
          <id>rehemiau</id>
          <name>Michał Łępicki</name>
          <url>https://github.com/michallepicki</url>
        </developer>
        <developer>
          <id>ark_adius</id>
          <name>Arek Burdach</name>
          <url>https://github.com/arkadius</url>
        </developer>
      </developers>
  }
)

val akkaV = "2.4.4"
val akkaStreamsV = "1.0"
val json4sV = "3.3.0"
val scalaTestV = "3.0.0-M7"
val swaggerCoreV = "1.5.8"

lazy val proj = (project in file(".")).
  settings(commonSettings).
  settings(publishSettings).
  settings(
    name := "akka-http-swagger",
    libraryDependencies ++= {
      Seq(
        "com.typesafe.akka"       %% "akka-http-experimental"        % akkaV,
        "io.swagger"              %  "swagger-core"                  % swaggerCoreV,
        "io.swagger"              %% "swagger-scala-module"          % "1.0.2",
        "de.heikoseeberger"       %% "akka-http-json4s"              % "1.6.0" % Test,
        "org.json4s"              %% "json4s-jackson"                % json4sV % Test,
        "com.typesafe.akka"       %% "akka-http-testkit"             % "2.4.4"   % Test,
        "org.scalatest"           %% "scalatest"                     % scalaTestV % Test
      )
    }
  )

publishArtifact := false

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
