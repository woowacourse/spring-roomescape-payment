#!/bin/bash
set -e

APP_NAME="spring-roomescape-payment"
REPO_DIR="/home/ubuntu/$APP_NAME"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
BUILD_DIR="$REPO_DIR/build/libs"
BRANCH_NAME="step2"

echo "📦 배포 시작..."
cd "$REPO_DIR" || { echo "❌ 디렉토리 이동 실패"; exit 1; }

echo "📥 Git Pull"
git pull origin ${BRANCH_NAME}

echo "🏗️ Gradle Build"
./gradlew clean build -x test

echo "🛑 기존 프로세스 종료"
if pgrep -f "$JAR_NAME" > /dev/null; then
  pkill -f "$JAR_NAME"
  echo "→ 프로세스 종료 시도 중..."
  sleep 2  # 종료되기까지 잠깐 대기
else
  echo "→ 종료할 프로세스 없음"
fi

echo "🗂 JAR 복사"
LATEST_JAR=$(ls -t $BUILD_DIR/*.jar | grep -v 'plain' | head -n 1)
cp "$LATEST_JAR" "$REPO_DIR/$JAR_NAME"

echo "🚀 애플리케이션 실행"
nohup java -jar "$REPO_DIR/$JAR_NAME" > "$REPO_DIR/log.out" 2>&1 &

echo "✅ 배포 완료!"
