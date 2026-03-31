# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
