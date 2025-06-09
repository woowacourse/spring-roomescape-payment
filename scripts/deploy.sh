#!/usr/bin/env bash
set -euo pipefail

trap 'echo "❗오류 발생 (LINE:$LINENO CMD:$BASH_COMMAND)"' ERR

REPO=/home/ubuntu
PROJECT=spring-roomescape-payment
BRANCH=step2

cd "$REPO/$PROJECT"
git fetch --all
git checkout "$BRANCH"
git pull --ff-only

echo "[빌드] 시작"
./gradlew bootJar

cd build/libs
JAR_FILE=$(ls "${PROJECT}"-*.jar)
BACKUP_DIR="$REPO/$PROJECT/backup"
mkdir -p "$BACKUP_DIR"
cp "$JAR_FILE" "$BACKUP_DIR/${JAR_FILE}.$(date +%F_%T).bak"

CURRENT_PID=$(pgrep -f "$JAR_FILE" || true)
if [ -n "$CURRENT_PID" ]; then
  echo "[종료] PID:$CURRENT_PID"
  kill -TERM "$CURRENT_PID"
  sleep 5
  kill -KILL "$CURRENT_PID" 2>/dev/null || true
fi

echo "[배포] 새로운 버전 실행"
mkdir -p "$REPO/$PROJECT/log"
nohup java -jar "$JAR_FILE" \
  > "$REPO/$PROJECT/log/$(date +%F).out" \
  2> "$REPO/$PROJECT/log/$(date +%F).err" &

# 헬스체크 (최대 5회, 2초 간격)
for i in {1..5}; do
  if curl -sf http://localhost:8080/actuator/health; then
    echo "[헬스체크] 성공"
    exit 0
  fi
  sleep 2
done

echo "❗헬스체크 실패: 이전 버전 복원 필요"
# (필요 시 백업 복원 로직 추가)
exit 1
