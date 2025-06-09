#!/bin/bash

set -e  # 에러 발생 시 중단
APP_NAME="spring-roomescape-payment"
REPO_URL="https://github.com/soeun2537/spring-roomescape-payment.git"
BRANCH_NAME="step2"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"

echo "🛑 기존 실행 중인 애플리케이션 종료 중..."
if pgrep -f "$JAR_NAME" > /dev/null; then
  sudo pkill -f "$JAR_NAME"
  echo "✅ 프로세스 종료 완료"
else
  echo "ℹ️ 실행 중인 프로세스가 없습니다."
fi

echo "🔄 기존 프로젝트 삭제 중..."
if [ -d "$APP_NAME" ]; then
  sudo rm -rf "$APP_NAME"
  echo "✅ 삭제 완료"
else
  echo "ℹ️ 삭제할 디렉터리가 없습니다."
fi

echo "📥 Git 클론 중..."
git clone "$REPO_URL"
cd "$APP_NAME"

echo "🔀 브랜치 전환 중: $BRANCH_NAME"
git checkout $BRANCH_NAME

echo "🛠️ Gradle 빌드 중..."
./gradlew bootJar

cd build/libs

echo "🚀 애플리케이션 실행 중..."
nohup java -jar $JAR_NAME > ../nohup.out 2>&1 &

echo "✅ 배포 완료!"
echo "📄 로그 확인: tail -f "$APP_NAME"/build/nohup.out"
