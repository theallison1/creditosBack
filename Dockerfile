# Usar una imagen base con Java instalado
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR de la aplicación al contenedor
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Exponer el puerto en el que la aplicación escucha
# Cambia el puerto si tu aplicación usa uno diferente
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "demo.jar"]