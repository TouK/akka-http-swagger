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
package pl.touk.swaggerAkkaHttp.swgr

import _root_.io.swagger.converter.ModelConverters
import akka.http.scaladsl.marshalling
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.{ConjunctionMagnet, Directive}
import scala.collection.JavaConversions._
import io.swagger.models._

import pl.touk.swaggerAkkaHttp.SwaggerIntegration

import scala.reflect.ClassTag
import scala.reflect._


case class ApiOperation[T: ClassTag](operationId: String = "")(implicit
                                marshaller: marshalling.ToResponseMarshaller[T]){
  private val c = classTag[T]
  private var endpointHttpMethod = akka.http.scaladsl.model.HttpMethods.GET
  private var endpointPlainPath = ""
  private var endpointParameters = Seq[_root_.io.swagger.models.parameters.Parameter]()
  private var responses = scala.collection.mutable.Map[String, Response]()
  private var summary: String= ""
  private var description: String = ""

  def summary(s:String = ""): ApiOperation[T] = {
    summary = s
    this
  }

  def notes(n:String = ""): ApiOperation[T] = {
    description = n
    this
  }

  //TODO: response(code, schema, descritpion, headers)
  //TODO: parameter(in: query/body/etc, description, schema, required?)

  def path[L](m: akka.http.scaladsl.model.HttpMethod, pm: SwaggerPathMatcher[L]) = {
    endpointHttpMethod = m
    endpointPlainPath = pm.plainPath
    endpointParameters = pm.parameters ++ endpointParameters
    this.addOperationToSwagger()
    import akka.http.scaladsl.server.Directives
    import akka.http.scaladsl.server.util.TupleOps.Join._

//  TODO: what's missing from SwaggerPathMatcher trait? try adding "x" inside parameter below:

    val directive: Directive[L] =
      Directives.method(m) & Directives.path(pm)
    directive & ConjunctionMagnet.fromDirective(Directives.parameter())

  }

  private def addOperationToSwagger() = {

    val response = new Response()
    response.setDescription("successful operation")

    if(c!=classTag[Null]) {
      SwaggerIntegration.addDefinition[T]
      response.setSchema(ModelConverters.getInstance().readAsProperty(c.runtimeClass))
    }

    val operation = new _root_.io.swagger.models.Operation()
    if(!operationId.isEmpty) operation.setOperationId(operationId)
    if(!summary.isEmpty) operation.setSummary(summary)
    if(!description.isEmpty) operation.setDescription(description)
    operation.addResponse(c.runtimeClass.toString, response)

    operation.setParameters(endpointParameters)
    operation.setResponses(
      scala.collection.mutable.Map[String, Response](
        ("200", response)
      ) ++ responses
    )

    //TODO: automatically add models that appear in schema if they dont exist
    // in swagger definitions already? now user has to add them with addDefinition manually

    var swaggerPath = new Path()
    if(SwaggerIntegration.swagger.getPaths.containsKey("/"+endpointPlainPath))
      swaggerPath = SwaggerIntegration.swagger.getPath("/"+endpointPlainPath)
    else
      SwaggerIntegration.swagger.path("/"+endpointPlainPath, swaggerPath)
    addOperationToPath(endpointHttpMethod)

    def addOperationToPath(m: HttpMethod) = {
      m match {
        case GET =>
          swaggerPath.setGet(operation)
        case POST =>
          swaggerPath.setPost(operation)
        case PUT =>
          swaggerPath.setPut(operation)
        case DELETE =>
          swaggerPath.setDelete(operation)
        case OPTIONS =>
          swaggerPath.setOptions(operation)
      }

    }
  }
}