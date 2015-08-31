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
package pl.touk.swaggerAkkaHttp

import _root_.io.swagger.util.Json
import akka.http.scaladsl.server.Directives._
import io.swagger.models.{Info, Path, Swagger}
import scala.collection.JavaConversions._
import scala.reflect._

object SwaggerIntegration {

  var interface = "localhost"
  var port = 8080
  var info = new Info()
    .title("Auto-generated swagger spec")
    .version("0.0.0")

  implicit var swagger = new Swagger()
  swagger.basePath("/")
  var swaggerUIPath = "swagger"

  swagger.setPaths(scala.collection.mutable.Map[String,Path]())

  //initializes swagger
  def apiWithSwagger(endpoints: akka.http.scaladsl.server.Route) : akka.http.scaladsl.server.Route = {
    swagger.info(info)
      .host(interface+":"+port.toString)
    val swaggerUIRoute =
    pathPrefix(swaggerUIPath) {
      path("swagger.json"){
        akka.http.scaladsl.server.Directives.complete {
          Json.mapper().writeValueAsString(swagger)
        }
      } ~
        pathEnd{
          redirect(akka.http.scaladsl.model.Uri(swaggerUIPath+"/index.html?url=http://"+interface+":"+port+"/"+swaggerUIPath+"/swagger.json"), akka.http.scaladsl.model.StatusCodes.MovedPermanently)
        } ~
        getFromResourceDirectory("swagger-ui")
    }
    swaggerUIRoute ~ endpoints
  }

  def addDefinition[T : ClassTag](): Unit = {
    val c = classTag[T]
    import scala.collection.JavaConversions._
    val models = _root_.io.swagger.converter.ModelConverters.getInstance().read(c.runtimeClass)
    val simpleClassName = c.runtimeClass.getSimpleName
    if (!models.isEmpty)
      swagger.addDefinition(simpleClassName, models(simpleClassName))
  }
}