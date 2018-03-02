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

