#!/bin/bash
set -e

# 스크립트가 위치한 디렉토리의 상위 디렉토리를 프로젝트 루트로 설정
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_DIR="$(dirname "$SCRIPT_DIR")"

APP_NAME="spring-roomescape-payment"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
BUILD_DIR="$REPO_DIR/build/libs"
BRANCH_NAME="step2"

echo "🚀 배포 시작 - $(date '+%Y-%m-%d %H:%M:%S')"
echo "📂 프로젝트 디렉토리: $REPO_DIR"

# 1. 소스코드 업데이트
echo "📥 Git Pull"
cd "$REPO_DIR" || { echo "❌ 디렉토리 이동 실패"; exit 1; }
git pull origin $BRANCH_NAME

# 2. 빌드
echo "🏗️ Gradle Build"
# REST Docs 디렉토리 생성 (asciidoctor 오류 방지)
mkdir -p build/generated-snippets
./gradlew clean build -x test -x asciidoctor

# 3. 기존 프로세스 종료
echo "🛑 기존 프로세스 종료"
if pgrep -f "$JAR_NAME" > /dev/null; then
  echo "→ 프로세스 종료 중..."
  pkill -f "$JAR_NAME"
  sleep 3
else
  echo "→ 종료할 프로세스 없음"
fi

# 4. JAR 파일 복사
echo "🗂️ JAR 복사"
LATEST_JAR=$(ls -t $BUILD_DIR/*.jar | grep -v 'plain' | head -n 1)
cp "$LATEST_JAR" "$REPO_DIR/$JAR_NAME"

# 5. 애플리케이션 실행
echo "🚀 애플리케이션 실행"
mkdir -p logs
nohup java -jar "$REPO_DIR/$JAR_NAME" > logs/app.log 2>&1 &
NEW_PID=$!

# 6. 실행 확인
echo "🔍 실행 확인 중..."
sleep 5
if ps -p $NEW_PID > /dev/null; then
  echo "✅ 배포 완료! (PID: $NEW_PID)"
  echo "📋 로그 보기: tail -f logs/app.log"
else
  echo "❌ 실행 실패! 로그를 확인하세요."
  tail -20 logs/app.log
  exit 1
fi
