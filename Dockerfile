FROM eclipse-temurin:17-jdk-jammy
WORKDIR /opt/app
COPY target/*.jar *.jar
ENTRYPOINT ["java","-jar","/opt/app/*.jar"]