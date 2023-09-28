FROM eclipse-temurin:17-jdk-jammy
WORKDIR /opt/app
COPY target/*.jar auth-service.jar
ENTRYPOINT ["java","-jar","/opt/app/auth-service.jar"]