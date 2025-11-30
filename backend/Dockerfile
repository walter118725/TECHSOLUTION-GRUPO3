FROM eclipse-temurin:21-jdk-jammy as build
WORKDIR /app

COPY . ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/gestion-ventas-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
