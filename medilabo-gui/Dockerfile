# syntax=docker/dockerfile:1.6

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
ENV TZ=Europe/Paris \
    JAVA_OPTS="-Dspring.profiles.active=docker"

RUN if ! id -u spring >/dev/null 2>&1; then \
            groupadd -f spring 2>/dev/null || true; \
            useradd -m -s /bin/sh -g spring spring 2>/dev/null || true; \
        fi && [ -d /app ] && chown -R spring:spring /app || true
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

# En mode conteneur interne, on vise HTTP (SERVER_PORT=8080 via compose)
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5009"
EXPOSE 5009
# create log dir and set ownership so agents can read it
RUN mkdir -p /var/log/medilabo && chown -R spring:spring /var/log/medilabo || true
VOLUME ["/var/log/medilabo"]

USER spring
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
