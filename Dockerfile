# --- Stage 1: Build the app ---
FROM registry.access.redhat.com/ubi9/openjdk-17:1.21 AS build

WORKDIR /app
COPY . .

# Ensure Maven wrapper is executable
RUN chmod +x ./mvnw

# Build the application (no tests)
RUN ./mvnw clean package -DskipTests

# --- Stage 2: Create final runtime image ---
FROM registry.access.redhat.com/ubi9/openjdk-17:1.21

ENV LANGUAGE='en_US:en'

# Copy the Quarkus app from the builder stage
COPY --chown=185 --from=build /app/target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 --from=build /app/target/quarkus-app/*.jar /deployments/
COPY --chown=185 --from=build /app/target/quarkus-app/app/ /deployments/app/
COPY --chown=185 --from=build /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185

ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
