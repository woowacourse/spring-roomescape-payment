#!/bin/bash

JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
JAR_PATH="./build/libs/$JAR_NAME"

TARGET_BRANCH="step2"

echo "======================================="
echo "배포 시작: $(date +%Y-%m-%d-%H:%M:%S)"
echo "타겟 브랜치: $TARGET_BRANCH"
echo "======================================="

echo ">> Git Checkout & Pull 시작 ($TARGET_BRANCH)..."
git checkout $TARGET_BRANCH
git pull origin $TARGET_BRANCH
if [ $? -ne 0 ]; then
    echo ">> Git Pull 실패"
    exit 1
fi
echo ">> Git Pull 완료"

echo ">> Gradle Build 시작..."
chmod +x ./gradlew
./gradlew build -x test
if [ $? -ne 0 ]; then
    echo ">> Gradle Build 실패"
    exit 1
fi
echo ">> Gradle Build 완료"

echo ">> 현재 실행 중인 애플리케이션 PID 확인"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
    echo ">> 현재 실행 중인 애플리케이션이 없습니다."
else
    echo ">> 실행 중인 애플리케이션 종료 (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo ">> 배포: $JAR_PATH"
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &

sleep 3
NEW_PID=$(pgrep -f $JAR_NAME)
echo ">> 시작 완료 (PID: $NEW_PID)"
echo "======================================="
