#!/bin/bash

set -e  # 에러 발생 시 종료

# === 환경 변수 설정 ===
APP_DIR="$HOME/spring-roomescape-payment"
BRANCH=${1:-step2}
LOG_DIR="$HOME/logs"
mkdir -p "$LOG_DIR"

BUILD_LOG="$LOG_DIR/build_$(date '+%Y%m%d_%H%M%S').log"
APP_LOG="$LOG_DIR/app_$(date '+%Y%m%d_%H%M%S').log"

echo "===== 배포 스크립트 시작 ====="
echo "시간: $(date)"
echo "빌드 로그: $BUILD_LOG"
echo "앱 실행 로그: $APP_LOG"
echo ""

# === 프로젝트 디렉토리 이동 ===
echo ">>> 프로젝트 디렉토리 이동: $APP_DIR"
cd "$APP_DIR" || { echo "❌ 프로젝트 디렉토리 없음: $APP_DIR"; exit 1; }

# === Git 최신 코드 반영 ===
echo ">>> Git 브랜치 변경 및 pull: $BRANCH"
git fetch origin
git checkout -B "$BRANCH" origin/"$BRANCH"
git pull origin "$BRANCH"

# === Gradle 빌드 ===
echo ">>> Gradle bootJar 빌드 시작..."
./gradlew bootJar >> "$BUILD_LOG" 2>&1
echo "✅ 빌드 완료."

# === 기존 서버 종료 ===
JAR_PATH=$(ls build/libs/*.jar | head -n 1)
APP_NAME=$(basename "$JAR_PATH")

echo ">>> 기존 서버 종료 시도: $APP_NAME"
PID=$(pgrep -f "$APP_NAME" || true)

if [ -n "$PID" ]; then
  echo "서버 종료 중 (PID: $PID)..."
  kill "$PID"
  sleep 5
  if kill -0 "$PID" 2>/dev/null; then
    echo "⚠️ 정상 종료 실패 → 강제 종료 시도"
    kill -9 "$PID"
    echo "✅ 강제 종료 완료"
  else
    echo "✅ 정상 종료 완료"
  fi
else
  echo "실행 중인 서버 없음."
fi

# === 서버 실행 ===
echo ">>> 서버 실행 중..."
nohup java -jar "$JAR_PATH" > "$APP_LOG" 2>&1 &

echo ""
echo "===== 배포 완료 ====="
echo "새 PID: $!"
echo "앱 로그 위치: $APP_LOG"
echo "시간: $(date)"
