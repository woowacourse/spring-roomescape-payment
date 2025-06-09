#!/bin/bash

set -e  # 에러 발생 시 스크립트 즉시 종료

# 기본값 설정
PROJECT_DIR="${PROJECT_DIR:-spring-roomescape-payment}"
BRANCH_NAME="${BRANCH_NAME:-step2}"
JAR_NAME="${JAR_NAME:-spring-roomescape-payment-0.0.1-SNAPSHOT.jar}"
LOG_FILE="${LOG_FILE:-app.log}"

echo "[배포 시작]"

# 1. 프로젝트 디렉토리로 이동
cd "$PROJECT_DIR"

# 2. 브랜치 변경 및 최신 코드 받기
echo "[Git] 브랜치 전환: $BRANCH_NAME"
git checkout "$BRANCH_NAME"
git pull

# 3. 빌드
echo "[Gradle] 프로젝트 빌드"
./gradlew bootJar

# 4. 빌드된 jar 디렉토리로 이동
cd build/libs

# 5. 기존 실행 중인 프로세스 종료
echo "[프로세스 종료] 실행 중인 $JAR_NAME 종료 시도"
PID=$(pgrep -f "$JAR_NAME") || true
if [ -n "$PID" ]; then
  kill "$PID"
  echo "기존 프로세스($PID) 종료됨"
fi

# 6. 새로운 jar 실행
echo "[실행] $JAR_NAME 실행"
nohup java -jar "$JAR_NAME" > "$LOG_FILE" 2>&1 &

echo "[배포 완료] 백그라운드에서 실행 중입니다. 로그: $PWD/$LOG_FILE"

#7. 헬스체크
echo "[헬스체크] 10초 대기 후 확인..."
sleep 10

HEALTH_URL="http://localhost:8080/actuator/health"
RESPONSE=$(curl -s "$HEALTH_URL")
if echo "$RESPONSE" | grep -q '"status":"UP"'; then
  echo "[✅ 헬스체크 통과]"
else
  echo "[❌ 헬스체크 실패]"
  exit 1
fi
