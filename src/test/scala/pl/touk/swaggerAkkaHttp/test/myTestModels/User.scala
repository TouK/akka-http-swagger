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
package pl.touk.swaggerAkkaHttp.test.myTestModels

import java.util.Date

import io.swagger.annotations.{ApiModelProperty, ApiModel}

import scala.annotation.meta.field

case class Address(street: String, number: Int)
@ApiModel("User")
case class User(@(ApiModelProperty@field)(required = true) name: String,
                age: Int,
                @(ApiModelProperty@field)(dataType = "date", required = false)birthdate: Option[Date],
                //TODO: fix swagger to automatically infer class from Option instead of making it an Array
                //https://github.com/swagger-api/swagger-scala-module/issues/7 https://github.com/swagger-api/swagger-core/issues/416#issuecomment-120213174
                addresses: Array[Address]){
  require(!name.isEmpty, "All users must have a name!")
  //TODO: automatically add required = true if such constraint is present
}

object UserResource{
  import scala.concurrent.ExecutionContext
  import scala.concurrent.Future
  def getUser(name: String, age: Int): User = {
    User(name, age, birthdate = Some(new java.util.Date()), addresses = new Array[Address](0))
  }
  def getFutureUser(name: String, age: Int)(implicit ec: ExecutionContext): Future[User] = {
    Future{
      User(name, age, birthdate = Some(new java.util.Date()), addresses = new Array[Address](0))
    }
  }

  def getFutureOptionUser(name:String, age: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    Future{
      age match {
        case a if a < 0 => None
        case _ => Some(User(name, age, birthdate = Some(new java.util.Date()), addresses = new Array[Address](0)))
      }
    }
  }
}