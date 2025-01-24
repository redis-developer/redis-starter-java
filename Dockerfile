# Build stage
FROM maven:3.9.9-amazoncorretto-23-alpine AS build
WORKDIR /home/app
COPY . .
RUN mvn -f ./pom.xml clean package -Dmaven.test.skip

# Package stage
FROM amazoncorretto:23-alpine3.20 AS release
WORKDIR /home/app
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]