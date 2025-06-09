#!/bin/bash

set -e  # 에러 발생 시 스크립트 종료

# -------------------- 설정 --------------------
BRANCH_NAME="${BRANCH_NAME:-step2}"
JAR_NAME="${JAR_NAME:-spring-roomescape-payment-0.0.1-SNAPSHOT.jar}"
LOG_FILE="${LOG_FILE:-app.log}"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JAR_PATH="$PROJECT_ROOT/build/libs/$JAR_NAME"
LOG_PATH="$PROJECT_ROOT/$LOG_FILE"
HEALTH_URL="http://3.36.69.170:8080/actuator/health"
# ---------------------------------------------

echo "[배포 시작]"
cd "$PROJECT_ROOT"

# 1. Git 브랜치 전환 및 최신 코드
echo "[Git] 브랜치 전환 및 업데이트: $BRANCH_NAME"
git fetch origin
git checkout "$BRANCH_NAME"
git reset --hard "origin/$BRANCH_NAME"
git clean -fd

# 2. Gradle 빌드
echo "[Gradle] bootJar 실행"
./gradlew bootJar

# 3. RestDocs 마스킹 (Linux sed 기준)
echo "[RestDocs] 토큰 마스킹"
find build/generated-snippets -name "*.adoc" \
  -exec sed -i 's/token=[^;[:space:]]*/token={ACCESS_TOKEN}/g' {} +

# 4. 실행 중인 기존 프로세스 종료
echo "[프로세스 종료] 기존 jar 종료 시도"
PID=$(pgrep -f "$JAR_NAME" || true)
if [ -n "$PID" ]; then
  kill "$PID"
  echo "기존 프로세스($PID) 종료됨"
else
  echo "실행 중인 프로세스 없음"
fi

# 5. 새 jar 실행
echo "[실행] $JAR_NAME 실행"
nohup java -jar "$JAR_PATH" > "$LOG_PATH" 2>&1 &

echo "[배포 완료] 로그 파일: $LOG_PATH"

# 6. 헬스체크
echo "[헬스체크] 10초 대기 후 확인..."
sleep 10
RESPONSE=$(curl -s "$HEALTH_URL")
if echo "$RESPONSE" | grep -q '"status":"UP"'; then
  echo "[✅ 헬스체크 통과]"
else
  echo "[❌ 헬스체크 실패]"
  exit 1
fi
