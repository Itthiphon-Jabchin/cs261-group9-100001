#!/usr/bin/env bash
set -euo pipefail

APP_DIR="uni-jobs-portal"

echo "==> สร้างโปรเจกต์ Spring Boot $APP_DIR"
rm -rf "$APP_DIR"
mkdir -p "$APP_DIR"
cd "$APP_DIR"

# สร้าง pom.xml
cat > pom.xml <<'EOF'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>campusjobs</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>campusjobs</name>
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.4</version>
  </parent>
  <properties>
    <java.version>17</java.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
EOF

# โฟลเดอร์โค้ด
mkdir -p src/main/java/com/example/campusjobs
cat > src/main/java/com/example/campusjobs/CampusJobsApplication.java <<'EOF'
package com.example.campusjobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CampusJobsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusJobsApplication.class, args);
    }
}
EOF

# application.properties
mkdir -p src/main/resources
cat > src/main/resources/application.properties <<'EOF'
spring.application.name=campusjobs
spring.datasource.url=jdbc:postgresql://db:5432/campusjobs
spring.datasource.username=campus
spring.datasource.password=campus123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
EOF

# Dockerfile
cat > Dockerfile <<'EOF'
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/campusjobs-0.0.1-SNAPSHOT.jar"]
EOF

# docker-compose.yml
cat > docker-compose.yml <<'EOF'
version: "3.9"
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: campusjobs
      POSTGRES_USER: campus
      POSTGRES_PASSWORD: campus123
    ports:
      - "5432:5432"
EOF

echo "==> เสร็จสิ้น! โปรเจกต์ถูกสร้างใน $APP_DIR แล้ว"
echo "ขั้นตอนถัดไป:"
echo "cd $APP_DIR"
echo "docker compose up --build"
