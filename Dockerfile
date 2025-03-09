# Usar una imagen base con Maven y Java
FROM maven:3.8.6-jdk-17-slim AS build
WORKDIR /app

# Copiar el código fuente
COPY pom.xml .
COPY src ./src

# Compilar el proyecto y generar el JAR
RUN mvn clean package -DskipTests

# Usar una imagen base con Java para la ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Exponer el puerto en el que la aplicación escucha
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "demo.jar"]
