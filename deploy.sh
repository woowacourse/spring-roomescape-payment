#!/bin/bash

set -e

APP_NAME="spring-roomescape-payment"
JAR_NAME="$APP_NAME-0.0.1-SNAPSHOT.jar"
PROJECT_DIR="$HOME/cd/$APP_NAME"
JAR_DIR="$PROJECT_DIR/build/libs"
BRANCH_NAME="step2"

echo ""
echo "🚀 배포 스크립트 실행 시작"

cd "$PROJECT_DIR"
echo "📂 디렉토리 이동: $PROJECT_DIR"

echo "🔄 Git 브랜치 전환"
git checkout "$BRANCH_NAME"

echo "🔍 원격 저장소 fetch"
git fetch origin "$BRANCH_NAME"

LOCAL_HASH=$(git rev-parse @)
REMOTE_HASH=$(git rev-parse origin/"$BRANCH_NAME")

if [ "$LOCAL_HASH" = "$REMOTE_HASH" ]; then
  echo "✅ 로컬이 최신 상태입니다. git pull 생략"
else
  echo "⚠️  로컬이 최신 상태가 아닙니다. git pull 실행..."
  git pull origin "$BRANCH_NAME"
fi

echo "⚙️ Gradle 빌드 시작"
./gradlew bootJar

cd "$JAR_DIR"
echo "📦 JAR 디렉토리 이동: $JAR_DIR"

PID=$(pgrep -f "$JAR_NAME")
if [ -n "$PID" ]; then
  echo "🛑 기존 애플리케이션 종료 (PID: $PID)"
  kill -9 "$PID"
fi

echo "🚀 새 애플리케이션 실행 중..."
nohup java -jar "$JAR_NAME" &

echo "✅ 배포 완료!"
echo ""
