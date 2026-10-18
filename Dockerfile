# Stage 1: build the Angular webapp
FROM node:20 AS webapp-build
WORKDIR /usr/src/app
COPY tertiary-verify-webapp-ng/package*.json ./
RUN npm install
COPY tertiary-verify-webapp-ng/ ./
RUN npm run build

# Stage 2: build the Spring Boot service (Java 8)
FROM maven:3.8-eclipse-temurin-8 AS service-build
WORKDIR /usr/src/service
COPY tertiary-verify-service/pom.xml ./
RUN mvn -B dependency:go-offline
COPY tertiary-verify-service/src ./src
RUN mvn -B -DskipTests package

# Stage 3: Java 8 runtime to be copied into the MySQL-based final image
FROM eclipse-temurin:8-jre AS jre

# Stage 4: final image = MySQL 8 + Nginx (webapp) + JRE (service)
FROM mysql:8.0
RUN microdnf install -y nginx && microdnf clean all

COPY --from=jre /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk PATH=/opt/java/openjdk/bin:$PATH

# Nginx config and SSL certificates
COPY tertiary-verify-webapp-ng/config-selfsigned/default.conf /etc/nginx/conf.d/default.conf
COPY tertiary-verify-webapp-ng/config-selfsigned/fullchain.pem /etc/letsencrypt/live/tertiaryverify.dedicated.co.za/
COPY tertiary-verify-webapp-ng/config-selfsigned/chain.pem /etc/letsencrypt/live/tertiaryverify.dedicated.co.za/
COPY tertiary-verify-webapp-ng/config-selfsigned/cert.pem /etc/letsencrypt/live/tertiaryverify.dedicated.co.za/
COPY tertiary-verify-webapp-ng/config-selfsigned/privkey.pem /etc/letsencrypt/live/tertiaryverify.dedicated.co.za/

COPY --from=webapp-build /usr/src/app/dist/tertiary-verify-webapp /usr/share/nginx/html
COPY --from=service-build /usr/src/service/target/*.jar /app/tertiary-verify-service.jar

# Prod data, imported by the MySQL entrypoint only when the data directory is empty (first run)
COPY tertiary-verify-service/src/main/resources/database/tertiary_verify_prod.sql /docker-entrypoint-initdb.d/tertiary_verify_prod.sql

COPY docker-entrypoint-app.sh /docker-entrypoint-app.sh
RUN chmod +x /docker-entrypoint-app.sh

ENV MYSQL_DATABASE=tertiary_verify_prod

# Database files are persisted here: mount a volume so the import only happens once
VOLUME /var/lib/mysql

# 8083: webapp (https), 8082: service (https), 8081: service (http). MySQL is only bound to localhost.
EXPOSE 8083 8082 8081
ENTRYPOINT ["/docker-entrypoint-app.sh"]
