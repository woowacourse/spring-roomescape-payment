#!/bin/bash

set -e  # 에러 발생 시 스크립트 중단
set -o pipefail

### 변수 설정 ###
REPO_URL=REPO_URL="https://github.com/jumdo12/spring-roomescape-payment.git"
APP_NAME="spring-roomescape-payment"
BUILD_JAR="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
DEPLOY_DIR="$HOME/$APP_NAME"
LOG_FILE="$DEPLOY_DIR/nohup.log"

### 1. 저장소 클론 ###
if [ -d "$DEPLOY_DIR" ]; then
    echo "[INFO] 기존 디렉토리 제거"
    rm -rf "$DEPLOY_DIR"
fi

echo "[INFO] 저장소 클론 시작"
git clone "$REPO_URL" "$DEPLOY_DIR"

cd "$DEPLOY_DIR"

### 2. 빌드 ###
echo "[INFO] Gradle 빌드 시작"
./gradlew bootJar

### 3. 서버 실행 ###
cd build/libs

echo "[INFO] 기존 프로세스 종료 시도"
PID=$(pgrep -f "$BUILD_JAR")
if [ -n "$PID" ]; then
  echo "[INFO] 기존 프로세스 종료: $PID"
  kill -15 "$PID"
  sleep 2
fi

echo "[INFO] 앱 실행 시작"
nohup java -jar "$BUILD_JAR" > "$LOG_FILE" 2>&1 &

echo "[INFO] 배포 완료: 로그는 $LOG_FILE 확인"
