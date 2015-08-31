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
usage or copy a [MinimalExample 
template](src/test/scala/pl/touk/swaggerAkkaHttp/test/MinimalExample.scala).

(as of 31.08.2015):
####working:
* serve swagger-ui under a custom path
* create endpoints with method and path parameters
* set endpoint's default response class
* set endpoint's summary, notes
* show validator badge (if present at port 8002)
* integrate with normal akka-http routes

####not working:
* create query and body parameters
* define file responses, other responses than json
* define alternative responses (400, 404 etc)
* set swagger tags
* defining endpoints breaks IntelliJ IDEA highlighting

-----------
