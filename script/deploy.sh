#!/bin/bash

# 배포할 브랜치명
BRANCH="step2"

# 프로젝트 디렉토리
PROJECT_DIR="spring-roomescape-payment"

echo "▶ 배포 시작..."

# 프로젝트 디렉토리 이동
cd "$PROJECT_DIR" || { echo "❌ 디렉토리를 찾을 수 없습니다: $PROJECT_DIR"; exit 1; }

# 브랜치 변경 및 최신 코드 가져오기
echo "▶ Git 브랜치 체크아웃: $BRANCH"
git checkout "$BRANCH"
git pull origin "$BRANCH"

# 빌드
echo "▶ Gradle로 bootJar 실행"
./gradlew bootJar || { echo "❌ Gradle 빌드 실패"; exit 1; }

# 이전 프로세스 종료 (선택 사항)
echo "▶ 기존 실행 중인 프로세스 종료 시도..."
PID=$(pgrep -f 'spring-roomescape-payment-0.0.1-SNAPSHOT.jar')
if [ -n "$PID" ]; then
  echo "🔴 기존 프로세스 종료: PID=$PID"
  kill "$PID"
else
  echo "ℹ️ 실행 중인 프로세스 없음"
fi

# JAR 파일 실행
cd build/libs || { echo "❌ JAR 파일 경로 없음"; exit 1; }
echo "▶ JAR 실행"
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

echo "✅ 배포 완료"