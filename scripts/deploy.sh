#!/bin/bash

REPO_URL="<https://github.com/awrion3/spring-roomescape-payment.git>"
PROJECT_NAME="spring-roomescape-payment"
BRANCH_NAME="step2"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"

echo "=== 1. Git Clone 또는 Pull ==="
if [ -d "$PROJECT_NAME" ]; then
  echo "기존 폴더 존재. 관련 프로세스 수행..."
  cd "$PROJECT_NAME"
  git fetch origin
  git checkout "$BRANCH_NAME"
  git pull origin "$BRANCH_NAME"
else
  echo "기존 폴더 없음. 관련 프로세스 수행..."
  git clone "$REPO_URL"
  cd "$PROJECT_NAME"
  git checkout "$BRANCH_NAME"
fi

echo "=== 2. Gradle bootJar 빌드 ==="
./gradlew bootJar

echo "=== 3. 기존 8080 포트 종료 ==="
PID=$(lsof -ti :8080)
if [ -n "$PID" ]; then
  echo "PID $PID 정상 종료 시도"
  kill -15 "$PID"
  sleep 3
  if kill -0 "$PID" 2>/dev/null; then
    echo "PID $PID 강제 종료"
    kill -9 "$PID"
  fi
else
  echo "8080 포트 사용 중인 프로세스 없음"
fi

echo "=== 4. JAR 파일 실행 ==="
cd build/libs
rm -f nohup.out
nohup java -jar "$JAR_NAME" > nohup.out 2>&1 &

echo "=== 배포 완료 ==="
