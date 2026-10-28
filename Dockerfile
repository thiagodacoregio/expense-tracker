# ---------- Stage 1: build ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy only the build files first, to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Now copy the source code and build the jar
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Run as a non-root user for security
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /app/target/*.jar app.jar

USER spring
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "app.jar"]