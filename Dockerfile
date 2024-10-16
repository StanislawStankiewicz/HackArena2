# Use Maven with OpenJDK 21 for building the project
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and download dependencies (this helps in caching dependencies)
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the project files
COPY src ./src

# Package the project, skipping tests if necessary
RUN mvn clean package -DskipTests

# Use a lightweight base image for runtime
FROM eclipse-temurin:21-jre-alpine

# Set the working directory for the runtime
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/MonoTanksClient-1.0-SNAPSHOT.jar ./MonoTanksClient.jar

# Copy the data directory. Developers can place their files in this directory and application will have access to them.
COPY ./data /app/data

# Command to run the application
ENTRYPOINT ["java", "-jar", "MonoTanksClient.jar"]