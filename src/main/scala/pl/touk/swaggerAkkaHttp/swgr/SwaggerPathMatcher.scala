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
package pl.touk.swaggerAkkaHttp.swgr

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatcher.Matching
import akka.http.scaladsl.server.util.Tuple
import akka.http.scaladsl.server.util.TupleOps.Join
import akka.http.scaladsl.server.{PathMatcher, PathMatcher0}

import scala.language.implicitConversions

trait SwaggerPathMatcher[L] extends PathMatcher[L] {
  self =>

  var plainPath = ""
  var parameters = Seq[io.swagger.models.parameters.Parameter]()

  override def / : SwaggerPathMatcher[L] = this ~ Slash

  def /[R](other: SwaggerPathMatcher[R])(implicit join: Join[L, R]): SwaggerPathMatcher[join.Out] =
    this ~ Slash ~ other

  def ~[R](other: SwaggerPathMatcher[R])(implicit join: Join[L, R]): SwaggerPathMatcher[join.Out] = {
    implicit def joinProducesTuple = Tuple.yes[join.Out]
    val ret = transform(_.andThen((restL, valuesL) ⇒ other(restL).map(join(valuesL, _))))
    ret.plainPath = this.plainPath + other.plainPath
    ret.parameters = this.parameters ++ other.parameters
    ret
  }

  override def transform[R: Tuple](f: Matching[L] ⇒ Matching[R]): SwaggerPathMatcher[R] =
    new SwaggerPathMatcher[R] { def apply(path: Path) = f(self(path)) }

}

object Slash extends SwaggerPathMatcher[scala.Unit] {
  plainPath = "/"
  def apply(path: akka.http.scaladsl.model.Uri.Path) =
    akka.http.scaladsl.server.PathMatchers.Slash.apply(path)
}

case class StringMatcher(s: String) extends SwaggerPathMatcher[scala.Unit]{
  plainPath = s
  def apply(path: akka.http.scaladsl.model.Uri.Path) : akka.http.scaladsl.server.PathMatcher.Matching[Unit] =
  {
    def toMatcher(m: PathMatcher0) = m
    toMatcher(s).apply(path)
  }
}

case class Segment(paramName: String, description: String = "") extends SwaggerPathMatcher[Tuple1[String]] {
  plainPath = "{"+paramName+"}"
  val param = new io.swagger.models.parameters.PathParameter()
  param.setType("string")
  param.setName(paramName)
  if(!description.isEmpty)param.setDescription(description)
  parameters = parameters :+ param
  def apply(path:akka.http.scaladsl.model.Uri.Path) =
    akka.http.scaladsl.server.PathMatchers.Segment.apply(path)
}

case class IntNumber(paramName: String, description: String = "") extends akka.http.scaladsl.server.PathMatchers.NumberMatcher[Int](Int.MaxValue, 10) with SwaggerPathMatcher[Tuple1[Int]] {
  plainPath = "{"+paramName+"}"
  val param = new io.swagger.models.parameters.PathParameter()
  param.setType("integer")
  param.setFormat("int32")
  param.setName(paramName)
  param.setDescription(description)
  parameters :+ param
  def fromChar(c: Char) = fromDecimalChar(c)
}

case class LongNumber(paramName: String, description: String = "") extends akka.http.scaladsl.server.PathMatchers.NumberMatcher[Long](Long.MaxValue, 10) with SwaggerPathMatcher[Tuple1[Long]] {
  plainPath = "{"+paramName+"}"
  val param = new io.swagger.models.parameters.PathParameter()
  param.setType("integer")
  param.setFormat("int64")
  param.setName(paramName)
  param.setDescription(description)
  parameters :+ param
  def fromChar(c: Char) = fromDecimalChar(c)
}
