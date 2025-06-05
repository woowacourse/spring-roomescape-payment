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
