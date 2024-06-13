FROM bellsoft/liberica-openjdk-alpine:17-aarch64

USER root
COPY ./build/libs/*SNAPSHOT.jar /app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]
