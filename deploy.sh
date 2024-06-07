#!/bin/bash

git checkout step2

echo "> Git Pull"
git pull origin step2

echo "> 프로젝트 Build 시작"
./gradlew bootJar

echo "> JAR 위치로 이동"
cd build/libs

echo "> 애플리케이션 실행"
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &

