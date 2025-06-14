#!/bin/bash

set -e
set -o pipefail

BRANCH_NAME="step1"
REMOTE_NAME="origin"
BUILD_JAR="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"

# 1. 기존 서버 종료
echo "[INFO] 실행 중인 서버 확인 중..."
PID=$(pgrep -f "$BUILD_JAR" || true)

if [ -n "$PID" ]; then
  echo "[INFO] 기존 서버 프로세스 종료 시도: PID=$PID"
  kill -15 "$PID"
  sleep 2

  if ps -p "$PID" > /dev/null; then
    echo "[WARN] 정상 종료 실패 → 강제 종료 시도"
    kill -9 "$PID"
  fi

  echo "[INFO] 기존 서버 종료 완료"
else
  echo "[INFO] 종료할 서버 프로세스 없음"
fi
git pull $REMOTE_NAME $BRANCH_NAME;
echo "최신 커밋 불러오기 완료";

echo "[INFO] Gradle 빌드 시작"
./gradlew bootJar

echo "[INFO] 앱 실행 시작"
cd build/libs
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &

echo "[INFO] 백그라운드 실행 완료"