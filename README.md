# javaMicroservicePOC

The aim of this project is show up how to develop a modern Java microservice. 
When we talk about microservices, we are talking about the following properties:

- Ubiquitous language
- Well defined model and boundaries
- Single responsability
- Async communication (NIO threads or eventLoop)
- Own their data
- Independent deployable, horizontal scalable and resilient

So we need a framework that support several language and also with a non-blocking threads or event loop philosophy.
Also the framework must be lightweight, in order to be able to scale in an horizontal way, without waste to many resources and resilient, so if the microservice is down, then the microservice must try to recover by his self.  

On the other hand this microservice must be in a shape, that would be posible to deploy in any platform. 

In order to fit all of this requirements the following technologies was selected. 

- [Vertx](https://vertx.io/) as an asynchronous lightweight framework
- Java9
- Docker

## How to develop your custom JRE

Java9 is divided in modules. In the most of your microservice you will not need all the modules. For example, you will not require Java swing.
So instead of create a docker image with the full JRE, you could create your custom JRE with just the modules that you need. To do that you will need to have a look to the command *jlink*.
One thing that you must know, is that the custom JRE that you are going to generated is platform dependent. That means that you can't use this JRE in an Alpine distribution if you generate this JRE in a Ubuntu distribution. 
So in order to create a custom JRE for Alpine you will need to use a Docker image with Java9 and Alpine, then generate your JRE with the modules that you need, and then move on this JRE to your final Docker images, that also contain your microservice. 

Let me give you an example

```
# First stage: Runs JLink to create the custom JRE
FROM jfisbein/alpine-oracle-jdk9 AS builder

WORKDIR /app

COPY target/*fat.jar app.jar

RUN jlink --module-path app.jar:$JAVA_HOME/jmods \
        --add-modules java.logging,java.base \
        --output dist \
        --compress 2 \
        --strip-debug \
        --no-header-files \
        --no-man-pages


# Second stage: setup your service over your custom JRE
FROM alpine:3.6

COPY src/main/resources/server-keystore.jks server-keystore.jks

WORKDIR /app
EXPOSE 8080

COPY --from=builder /app/dist/ ./
COPY target/*fat.jar app.jar

ENTRYPOINT [ "sh", "-c", "/app/bin/java -jar /app/app.jar" ]
```

You could test this example running an instance of this microservice in your localhost. 

```
docker pull pjgg/java9-microservice-poc
docker run -d -p 8080:8080 pjgg/java9-microservice-poc:latest
curl -v http://localhost:8080/
```

## Conclusion

Have a look the size in memory of your Java microservice. It's about 40Mb. 
Operative system + 1.9JRE + microservice(with vertxCore) just 40MB. 

How can be possible?. Basically is possible because we remove from java JDK all the modules that we are not going to use. In other words, in order to develop this microservice we required from Java, javaBase and javaLogging, that is all. 
The first result about this approach is that you are going to reduce the RAM memory that you are using and also your *cloud provider bill will be reduced in the same percentage*. Remember that the RAM memory is the most expensive resource that we have. 

If for example you use SpringBoot  with Java1.8, then you will need at least 200Mb of Ram memory. Basically because Java1.8 need 200Mb. So if you scale your service to 10 instance, then you will spend 2Gb of Ram memory. The same microservice developed in Java1.9 with a custom JRE and a modern framework will required just 200Mb (10 instances). 

Thats a lot of money saved!. 
