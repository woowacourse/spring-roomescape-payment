#!/bin/bash

git checkout step2

echo "> Git Pull"
git pull origin step2

echo "> 프로젝트 Build 시작"
./gradlew bootJar

echo "> JAR 위치로 이동"
cd build/libs

echo "> 현재 실행 중인 프로세스 확인"
EXISTING_PROCESS_ID=$(lsof -ti tcp:8080)

if [ -n "$EXISTING_PROCESS_ID" ]; then
    echo "> 기존에 8080 포트를 사용 중인 프로세스를 종료합니다."
    kill -15 $EXISTING_PROCESS_ID
    sleep 5
fi

echo "> 애플리케이션 실행"
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &

