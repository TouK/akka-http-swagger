/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.touk.swaggerAkkaHttp.test

import pl.touk.swaggerAkkaHttp.test.myTestModels.{Address, User, UserResource}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import pl.touk.swaggerAkkaHttp.SwaggerIntegration._

object Example extends App  {

  import akka.http.scaladsl.server.Directives.{segmentStringToPathMatcher=>_,_}
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import Json4sSupport._
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats

  import system.dispatcher

  interface = "localhost"
  port = 8080
  info.title("Example app")
    .description("description")
  swaggerUIPath = "swgr"


  val route: Route = {

    apiWithSwagger {
      import pl.touk.swaggerAkkaHttp.swgr._
      import pl.touk.swaggerAkkaHttp.swgr.SwaggerMatcherImplicits._
      //implicit conversion is needed for writing simply "user" / "something" instead of StringMatcher("user") / StringMatcher("something")
      //so we have to exclude segmentStringToPathMatcher conversion to avoid ambiguity
      ApiOperation[User]("GetUserByName")
        .summary("Get user with given name")
        .notes("This is an implementation note")
        .path(GET, "user" / Segment("userName") ) {
          name =>
            complete {
              UserResource.getFutureUser(name, age = 12)
            }
        } ~
        ApiOperation[Array[User]]()
          .path(GET, "users") {
          complete {
            scala.concurrent.Future{Array[User](UserResource.getUser("name", age = 12))}
          }
        } ~
        ApiOperation[Null]()
          .path(POST,"empty") {
          complete {""}
        }
    } ~
    {
      import akka.http.scaladsl.server.Directives.segmentStringToPathMatcher
      get{
          complete{
            UserResource.getUser("defaultResponse",age=666)
          }
        }
    }
  }
  addDefinition[Address]

  val serverBinding = Http(system).bindAndHandle(route, interface, port)
}
/*
  NOTES:
  add these to your build.sbt dependencies:
  "de.heikoseeberger" %% "akka-http-json4s" % "1.0.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11"
 */