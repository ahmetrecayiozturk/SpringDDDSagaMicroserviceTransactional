# Use a base image that has OpenJDK 21
FROM openjdk:21-jdk-slim as builder

# Set the working directory for Gradle
WORKDIR /app

# Copy the Gradle wrapper files and build.gradle files
COPY gradle /app/gradle
COPY gradlew /app
COPY app/build.gradle /app
COPY settings.gradle /app

# Copy the rest of the project source code
COPY app/src /app/src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the project using Gradle
RUN ./gradlew build --no-daemon

# Now, use a fresh OpenJDK 21 image to run the app
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the build step (from the builder image)
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Expose the port your application will run on (adjust if necessary)
EXPOSE 8080

# Run the Java application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
