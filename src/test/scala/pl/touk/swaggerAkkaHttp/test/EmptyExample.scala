/*
 * Copyright 2015 [TouK sp. z o.o. s.k.a](http://www.touk.pl/)
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

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import pl.touk.swaggerAkkaHttp.SwaggerIntegration._

object MyApp extends App {

  import akka.http.scaladsl.server.Directives.{segmentStringToPathMatcher=>_,_}
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import Json4sSupport._
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = DefaultFormats
  import system.dispatcher

  interface = "localhost"
  port = 8080


  val route: Route = {
    apiWithSwagger {
      import pl.touk.swaggerAkkaHttp.swgr._
      ApiOperation[returnClass]()
        .path(method, "endpoint" / "path"){
          //akka-http normal routing for this operation
      }
    } ~
    {
      import akka.http.scaladsl.server.Directives.segmentStringToPathMatcher
     //akka-http normal not documented routing
    }
  }
  addDefinition[anotherClass]

  val serverBinding = Http(system).bindAndHandle(route, interface, port)
}
