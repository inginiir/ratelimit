FROM openjdk:17-jdk-slim
ENV ARTIFACT_NAME='ratelimit-0.0.1-SNAPSHOT.jar'

COPY ./build/libs/$ARTIFACT_NAME .

EXPOSE 8080
ENTRYPOINT exec java -jar ${ARTIFACT_NAME}