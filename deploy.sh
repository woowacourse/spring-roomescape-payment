#!/bin/bash

PROJECT_PATH=/home/ubuntu/spring-roomescape-payment
PROJECT_NAME=spring-roomescape-payment
PROJECT_BRANCH=step2
JAR_DIR=build/libs

if [ -z "$PROJECT_PATH" ] || [ -z "$JAR_DIR" ] || [ -z "$PROJECT_NAME" ]; then
  echo "$(date +"%Y-%m-%d %H:%M:%S"): 환경 변수가 제대로 설정되지 않았습니다."
  exit 1
fi

cd $PROJECT_PATH
git reset --hard HEAD

echo "$(date +"%Y-%m-%d %H:%M:%S"): Github에서 최신 코드를 가져옵니다."
git pull origin $PROJECT_BRANCH || { echo "$(date +"%Y-%m-%d %H:%M:%S"): git pull에 실패했습니다."; exit 1; }

echo "$(date +"%Y-%m-%d %H:%M:%S"): 프로젝트를 빌드합니다."
./gradlew bootJar || { echo "$(date +"%Y-%m-%d %H:%M:%S"): 프로젝트 빌드를 실패했습니다."; exit 1; }

CURRENT_PID=$(pgrep -f "${PROJECT_NAME}.*.jar")
if [ -n "$CURRENT_PID" ]; then
  echo "$(date +"%Y-%m-%d %H:%M:%S"): 현재 구동 중인 어플리케이션을 종료합니다. PID: $CURRENT_PID"
  kill -15 "$CURRENT_PID"
  sleep 5
else
  echo "$(date +"%Y-%m-%d %H:%M:%S"): 현재 구동 중인 어플리케이션이 없습니다."
fi

JAR_NAME=$(ls -t $PROJECT_PATH/$JAR_DIR/*.jar | head -n 1)
if [ -n "$JAR_NAME" ]; then
  echo "$(date +"%Y-%m-%d %H:%M:%S"): 어플리케이션을 실행합니다. JAR 파일: $JAR_NAME"
  nohup java -jar $PROJECT_PATH/$JAR_DIR/$JAR_NAME >> $PROJECT_PATH/nohup.out 2>&1 &
  echo "$(date +"%Y-%m-%d %H:%M:%S"): 어플리케이션 실행이 완료되었습니다."
else
  echo "$(date +"%Y-%m-%d %H:%M:%S"): 실행할 JAR 파일을 찾을 수 없습니다."
  exit 1
fi
