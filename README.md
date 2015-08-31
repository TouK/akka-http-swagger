#swaggerAkkaHttp
Related akka-http github issue [here](https://github.com/akka/akka/issues/16591)

Building RESTful [akka-http]
(http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0/scala/http/index.html)
APIs with [Swagger documentation](http://swagger.io/) can be achieved by
[applying specific annotations to resources and models]
(https://github.com/Tecsisa/akka-http-swagger#resource-definitions).
The aim of this project is to provide similar functionality, but reducing the number
of necessary swagger annotations (which force you to write the same information twice).

see an [Example](src/test/scala/pl/touk/swaggerAkkaHttp/test/Example.scala) for
usage or copy an [MinimalExample template](src/test/scala/pl/touk/swaggerAkkaHttp/test/MinimalExample.scala).

-----------

Copyright (c) 2015 [TouK sp. z o.o. s.k.a](http://www.touk.pl/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
