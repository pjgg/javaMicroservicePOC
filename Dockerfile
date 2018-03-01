FROM pjgg/custom-jre:v1.0.0

RUN apk update && \
    apk add openssl curl

COPY target/*fat.jar app.jar
RUN sh -c 'touch /app.jar'

EXPOSE 8080

RUN ls ./custom-jre/
RUN ls ./custom-jre/bin/

ENTRYPOINT [ "sh", "-c", "./custom-jre/bin/customJava -jar /app.jar" ]