#!/bin/bash
set -e  # 오류 발생 시 스크립트 즉시 종료

# === 설정 ===
APP_NAME="spring-roomescape-payment"
REPO_DIR="/home/ubuntu/$APP_NAME"
BRANCH_NAME="step2"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"  # bootJar로 생성된 파일명
LOG_FILE="$REPO_DIR/app.log"

echo "==== [$APP_NAME 배포 시작] ===="

# 1. 디렉토리 이동
cd "$REPO_DIR" || {
  echo "❌ 디렉토리 이동 실패: $REPO_DIR"
  exit 1
}

# 2. git pull
echo "✅ Git pull ($BRANCH_NAME)"
git fetch origin "$BRANCH_NAME"
git checkout "$BRANCH_NAME"
git pull origin "$BRANCH_NAME" || {
  echo "❌ git pull 실패"
  exit 1
}

# 3. 기존 애플리케이션 종료
echo "🛑 기존 애플리케이션 종료"
PID=$(ps -ef | grep "$JAR_NAME" | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
  kill -9 "$PID"
  echo "✔ 기존 프로세스 종료 (PID=$PID)"
else
  echo "ℹ 실행 중인 프로세스 없음"
fi

# 4. Gradle 빌드
echo "⚙️ Gradle 빌드 시작"
./gradlew clean bootJar -x test || {
  echo "❌ Gradle 빌드 실패"
  exit 1
}

# 5. 애플리케이션 실행
echo "🚀 앱 실행"
JAR_PATH="build/libs/$JAR_NAME"
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR 파일이 존재하지 않음: $JAR_PATH"
  exit 1
fi

nohup java -jar "$JAR_PATH" >> "$LOG_FILE" 2>&1 &

echo "📄 로그 파일: $LOG_FILE"
echo "==== ✅ 배포 완료 ===="
