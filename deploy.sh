#!/bin/bash

JAR_DIRECTORY=build/libs
PROJECT_NAME=spring-roomescape-payment
LOG_DIR=/home/ubuntu

echo "> Git Pull"
git pull origin step2

echo "> 프로젝트 Build 시작"
./gradlew bootJar

echo "> JAR 위치로 이동"
cd ./$JAR_DIRECTORY

echo "> 현재 구동중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"
if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr / | grep jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"
nohup java -jar $JAR_NAME --spring.profiles.active=prod > $LOG_DIR/application_log.out 2>&1 &
