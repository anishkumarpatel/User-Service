FROM bellsoft/liberica-openjdk-alpine:17
RUN apk upgrade
VOLUME /tmp
RUN addgroup --system --gid 1002 udb
RUN adduser --system --uid 1002 udb
ARG JAR_FILE=target/*.jar
COPY --chown=udb:udb ${JAR_FILE} /udb-user-service.jar
RUN jar -xf /udb-user-service.jar

RUN cp /BOOT-INF/lib/dd-java-agent-0.98.0.jar /dd-java-agent-0.98.0.jar
RUN rm -rf /BOOT-INF


USER udb
ENTRYPOINT ["java","-javaagent:/dd-java-agent-0.98.0.jar","-jar","/udb-user-service.jar"]