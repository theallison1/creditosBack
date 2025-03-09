# Usa OpenJDK 17 como base para la etapa de construcción
FROM openjdk:17-jdk-slim AS build

# Instalar Maven manualmente
RUN apt-get update && apt-get install -y maven

# Establece el directorio de trabajo
WORKDIR /app

# Copia el código fuente de la aplicación
COPY pom.xml .
COPY src ./src

# Ejecuta el comando de Maven para construir el JAR
RUN mvn clean package -DskipTests

# Usa OpenJDK 17 para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR generado desde la etapa de construcción
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Exponer el puerto en el que la aplicación escucha
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "demo.jar"]
