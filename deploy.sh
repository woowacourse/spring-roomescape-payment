#!/bin/bash

APP_NAME="spring-roomescape-payment"
BUILD_DIR="./build/libs"
LOG_DIR="./logs"
BRANCH_NAME="step2"

echo "새로운 버전 업데이트"
git fetch origin
git switch $BRANCH_NAME || git checkout $BRANCH_NAME
git pull origin $BRANCH_NAME

echo "JAR 빌드"
./gradlew clean bootJar

if [ $? -ne 0 ]; then
  echo "빌드 실패"
  exit 1
fi

JAR_PATH=$(ls $BUILD_DIR/*.jar | head -n 1)

if [ ! -f "$JAR_PATH" ]; then
  echo "JAR 파일이 없습니다: $JAR_PATH"
  exit 1
fi

echo "실행 중인 앱 종료"
PID=$(pgrep -f "$JAR_PATH")
if [ -n "$PID" ]; then
  kill -15 $PID
  echo "→ 종료 완료 (PID: $PID)"
  sleep 5
fi

echo "앱 실행"
mkdir -p $LOG_DIR
nohup java -jar "$JAR_PATH" \
  --spring.profiles.active=prod \
  2> $LOG_DIR/error.log &

sleep 3

NEW_PID=$(pgrep -f "$JAR_PATH")

if [ -n "$NEW_PID" ]; then
  echo "앱 실행 성공 (PID: $NEW_PID)"
else
  echo "앱 실행 실패 $LOG_DIR/error.log"
  exit 1
fi
