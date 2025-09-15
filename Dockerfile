# Use an official OpenJDK 21 runtime as a parent image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file into the container
COPY build/libs/newwork-be-*.jar app.jar

# Expose the application port
EXPOSE 8081

# Set the Spring profile to 'local' by default
ENV SPRING_PROFILES_ACTIVE=local

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
