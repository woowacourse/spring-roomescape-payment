#!/bin/bash

set -e

# 배포 디렉토리 설정
DEPLOY_DIR="/home/ubuntu"
PROJECT_NAME="spring-roomescape-payment"
PROJECT_DIR="$DEPLOY_DIR/$PROJECT_NAME"

echo "🚀 배포 시작: $PROJECT_NAME"

cd $DEPLOY_DIR

# Git 저장소 업데이트
echo "📦 코드 업데이트 중..."
if [ -d "$PROJECT_DIR" ]; then
    cd $PROJECT_DIR
    git stash
    git pull origin step2
    git stash pop
    echo "✅ Git pull 완료"
else
    git clone -b step2 https://github.com/KoSeonJe/spring-roomescape-payment.git
    if [ -d "$PROJECT_DIR" ]; then
        cd $PROJECT_DIR
        echo "✅ Git clone 완료"
    else
        echo "❌ Git clone 실패"
        exit 1
    fi
fi

# 기존 애플리케이션 프로세스 종료
echo "⏹️  기존 프로세스 종료 중..."
PID=$(ps aux | grep java | grep $PROJECT_NAME | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
    kill -9 $PID > /dev/null 2>&1
    sleep 2
    echo "✅ 기존 프로세스 종료됨 (PID: $PID)"
else
    echo "ℹ️  실행 중인 프로세스 없음"
fi

# gradlew 실행 권한 확인
if [ ! -x "./gradlew" ]; then
    echo "🔧 gradlew 실행 권한 설정 중..."
    chmod +x ./gradlew
fi

# Gradle 빌드 (테스트 포함)
echo "🔨 빌드 중..."
./gradlew bootJar -x test
echo "✅ 빌드 완료"

# JAR 파일 폴더로 이동
cd build/libs

# 백그라운드에서 애플리케이션 실행
echo "🚀 애플리케이션 시작 중..."
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &

echo "🚀 배포 완료"
