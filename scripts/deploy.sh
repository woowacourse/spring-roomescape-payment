#!/bin/bash

set -e  # 에러 발생 시 스크립트 즉시 종료

APP_DIR=~/spring-roomescape-payment
BRANCH=${1:-step2}  # 인자로 브랜치명 주지 않으면 step2 사용
LOG_DIR=~/logs
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/deploy_$(date '+%Y%m%d_%H%M%S').log"

echo "===== 배포 스크립트 시작 ====="
echo "시간: $(date)"
echo "로그파일: $LOG_FILE"
echo ""

# 1. 프로젝트 디렉토리 이동
echo ">>> 프로젝트 디렉토리 이동: $APP_DIR"
cd "$APP_DIR" || { echo "❌ 프로젝트 디렉토리 없음: $APP_DIR"; exit 1; }

# 2. 최신 코드 가져오기
echo ">>> Git 브랜치 변경: $BRANCH"
git fetch origin
git checkout "$BRANCH"
echo ">>> 최신 코드 pull 중..."
git pull origin "$BRANCH"

# 3. Gradle 빌드
echo ">>> Gradle 빌드 시작..."
./gradlew bootJar >> "$LOG_FILE" 2>&1
echo ">>> Gradle 빌드 완료."

# 4. 기존 서버 종료 (Graceful shutdown 시도 후 강제 종료)
JAR_NAME=$(ls build/libs/*.jar | head -n 1)
echo ">>> 기존 서버 종료 시도: $JAR_NAME"
PID=$(pgrep -f "$JAR_NAME" || true)
if [ -n "$PID" ]; then
  echo "서버 종료 중 (PID: $PID)..."
  kill "$PID"  # SIGTERM 보내기
  sleep 5
  if kill -0 "$PID" 2>/dev/null; then
    echo "서버가 종료되지 않아 강제 종료 중..."
    kill -9 "$PID"
  fi
else
  echo "실행 중인 서버 없음."
fi

# 5. 서버 실행
echo ">>> 서버 실행 중..."
nohup java -jar "$JAR_NAME" > "$LOG_FILE" 2>&1 &

echo ""
echo "===== 배포 완료 ====="
echo "서버 PID: $!"
echo "로그 위치: $LOG_FILE"
echo "시간: $(date)"
