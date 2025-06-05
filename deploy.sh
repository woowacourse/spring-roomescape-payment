#!/bin/bash

set -e
set -o pipefail

REPO_URL="https://github.com/jumdo12/spring-roomescape-payment.git"
APP_NAME="spring-roomescape-payment"
BUILD_JAR="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
DEPLOY_DIR="$HOME/$APP_NAME"
TEMP_DIR="$HOME/${APP_NAME}_tmp"
LOG_FILE="$DEPLOY_DIR/nohup.log"

echo "[INFO] 임시 디렉토리 생성 및 저장소 클론"
rm -rf "$TEMP_DIR"
git clone "$REPO_URL" "$TEMP_DIR"

echo "[INFO] 기존 앱 백업 및 교체"
if [ -d "$DEPLOY_DIR" ]; then
  BACKUP_DIR="$HOME/${APP_NAME}_backup_$(date +%s)"
  mv "$DEPLOY_DIR" "$BACKUP_DIR"
  echo "[INFO] 기존 디렉토리 백업 → $BACKUP_DIR"
fi

mv "$TEMP_DIR" "$DEPLOY_DIR"

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