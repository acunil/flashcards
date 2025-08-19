# Build stage
FROM maven:3.9.9-amazoncorretto-24-alpine AS build
WORKDIR /workspace

# Copy only pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build layered jar
COPY src ./src
RUN mvn clean package -Pprod -DskipTests

# Extract layers from the boot jar
RUN java -Djarmode=layertools -jar target/*.jar extract

# Runtime stage
FROM amazoncorretto:24-alpine-jdk
WORKDIR /app

# Copy layers in order (best for Docker caching)
COPY --from=build /workspace/dependencies/ ./
COPY --from=build /workspace/spring-boot-loader/ ./
COPY --from=build /workspace/snapshot-dependencies/ ./
COPY --from=build /workspace/application/ ./

EXPOSE 8080

# Run with memory tuning
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]