#!/bin/sh
set -e

# 🛠️ 배포에 사용할 변수들
REMOTE_APP_DIR="/home/ec2-moru/spring-roomescape-payment"
GIT_REPO="https://github.com/Choidongjun0830/spring-roomescape-payment"
BRANCH="step2"
JAR_NAME="spring-roomescape-payment.jar"

echo "📁 작업 디렉토리로 이동"
mkdir -p ${REMOTE_APP_DIR}
cd ${REMOTE_APP_DIR}

if [ ! -d ".git" ]; then
  echo "🔄 Git 저장소 clone 중..."
  git clone -b ${BRANCH} ${GIT_REPO} .
else
  echo "🔄 Git pull로 최신화 중..."
  git checkout ${BRANCH}
  git pull origin ${BRANCH}
fi

echo "🔨 빌드 시작..."
./gradlew bootJar

echo "🧹 기존 프로세스 종료 중..."
pkill -f ${JAR_NAME} || echo "실행 중인 프로세스 없음"

echo "🚀 애플리케이션 실행 중..."
nohup java -jar build/libs/${JAR_NAME} > app.log 2>&1 &

echo "✅ 배포 완료!"