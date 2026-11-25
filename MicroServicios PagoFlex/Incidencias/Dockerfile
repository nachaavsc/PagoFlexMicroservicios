FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY ["MicroServicios PagoFlex/Incidencias/", "./"]
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
