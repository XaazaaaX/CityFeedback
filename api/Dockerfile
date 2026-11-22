# syntax=docker/dockerfile:1

# --- Build-Stage: JAR mit dem Maven Wrapper bauen ------------------------------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Zuerst nur Wrapper + pom kopieren, damit die Abhängigkeiten gecacht werden
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src/ src/
# Version der Anwendung (von semantic-release); leer = Wert aus der pom.xml
ARG REVISION=""
# Tests und Linting laufen bereits in der CI und werden hier übersprungen
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -ntp package -DskipTests -Dcheckstyle.skip ${REVISION:+-Drevision=$REVISION} \
 && cp target/*.jar app.jar \
 && java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# --- Runtime-Stage: nur JRE + Anwendung ----------------------------------------
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring --no-create-home spring

# Layer von selten zu häufig geändert -> bessere Docker-Cache-Nutzung
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
