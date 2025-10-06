# Estágio de Compilação
FROM openjdk:21-slim AS builder

WORKDIR /app

COPY mvnw .

COPY .mvn .mvn

COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw package -DskipTests


# Estágio de Execução
FROM openjdk:21-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]