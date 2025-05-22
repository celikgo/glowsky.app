# Stage 1: Build the application
FROM gradle:8.10-jdk21 AS build

WORKDIR /app

# Copy the entire project (including all services)
COPY . /app

# Ensure the Gradle wrapper is executable
RUN chmod +x gradlew

# Build the post-service specifically
RUN ./gradlew :post-service:clean :post-service:build --no-daemon

# Stage 2: Create the runtime image
FROM amazoncorretto:21

WORKDIR /app

# Create config directory
RUN mkdir -p /app/config

# Copy the built JAR from the build stage
COPY --from=build /app/post-service/build/libs/*.jar app.jar

# Copy the application.yml directly to the config directory
COPY post-service/src/main/resources/application.yml /app/config/

# Expose the port
EXPOSE 8081

# Run the application with explicit config location
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "app.jar", "--spring.config.location=file:/app/config/application.yml"]