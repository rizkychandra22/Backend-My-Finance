# Stage 1: Build aplikasi Java menggunakan Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Salin pom.xml dan folder src proyek
COPY pom.xml .
COPY src ./src

# Kompilasi proyek menjadi file WAR (abaikan pengujian/test untuk mempercepat build)
RUN mvn clean package -DskipTests

# Stage 2: Deploy & Jalankan aplikasi di Server WildFly
FROM quay.io/wildfly/wildfly:26.1.3.Final-jdk17

# Salin file WAR hasil kompilasi dari Stage 1 ke folder deployment WildFly
COPY --from=build /app/target/my-finance-backend.war /opt/jboss/wildfly/standalone/deployments/

# Buka port 8080
EXPOSE 8080

# Jalankan WildFly dengan pembatasan RAM (-Xmx350m) agar muat di RAM Free Tier Cloud (512 MB)
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-java-options", "-Xmx350m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=128m"]
