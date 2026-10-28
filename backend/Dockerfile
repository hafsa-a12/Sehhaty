# Étape 1 : compilation
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# Étape 2 : image d'exécution légère, sans root
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 1001 sehhaty
COPY --from=build /app/target/sehhaty-api-*.jar app.jar
USER sehhaty
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
