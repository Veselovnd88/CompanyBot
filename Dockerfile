FROM openjdk:17
ARG JAR_FILE=target/CompanyBot-0.0.1-SNAPSHOT.jar
ARG config=external-config.yml
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
COPY ${config} external-config.yml
ENTRYPOINT ["java","-jar","app.jar","--spring.config.location=file:/opt/app/external-config.yml"]