FROM maven:3.6.3-jdk-8 as build
WORKDIR /opt/build
COPY api .
ARG MONGO_DB_HOST
ENV MONGO_DB_HOST $MONGO_DB_HOST
ARG MONGO_DB_PORT
ENV MONGO_DB_PORT $MONGO_DB_PORT
ARG MONGO_DB_NAME
ENV MONGO_DB_NAME $MONGO_DB_NAME
ARG MONGO_DB_USERNAME
ENV MONGO_DB_USERNAME $MONGO_DB_USERNAME
ARG MONGO_DB_PASSWORD
ENV MONGO_DB_PASSWORD $MONGO_DB_PASSWORD
ARG MONGO_DB_AUTH_NAME
ENV MONGO_DB_AUTH_NAME $MONGO_DB_AUTH_NAME
ARG ZIPKIN_BASE_URL
ENV ZIPKIN_BASE_URL $ZIPKIN_BASE_URL
RUN mvn clean package -Dspring.profiles.active=docker

FROM openjdk:8-jdk-alpine
COPY --from=build /opt/build/target/*.jar app.jar
EXPOSE 8080
CMD java -XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /app.jar
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
