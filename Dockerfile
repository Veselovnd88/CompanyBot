FROM openjdk:17

MAINTAINER veselovnd@gmail.com

ARG JAR_FILE=build/libs/app.jar

WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]