# ---------- BUILD STAGE ----------
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy everything
COPY . .

# Give execute permission to mvnw (important for Linux!)
RUN chmod +x mvnw

# Build application (this creates the JAR inside /app/target)
RUN ./mvnw clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
