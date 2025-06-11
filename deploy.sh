#!/bin/bash

# ===== 옵션: 에러 발생 시 즉시 중단 =====
set -e

# ===== 설정 =====
REPO_URL="https://github.com/seaniiio/spring-roomescape-payment.git"
APP_NAME="spring-roomescape-payment"
BUILD_DIR="build/libs"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
LOG_FILE="/app.log"
PID_FILE="/app.pid"

# ===== 코드 업데이트 =====
echo ">>> 최신 코드 업데이트"
git fetch origin
git pull origin step2

# ===== 배포 시작 =====
echo ">>> Build 시작"
./gradlew bootJar

# ===== 기존 앱 종료 =====
cd $BUILD_DIR

if [ -f "../../$PID_FILE" ]; then
    PID=$(cat "../../$PID_FILE")
    if ps -p $PID > /dev/null; then
        echo ">>> 기존 앱 종료: PID $PID"
        kill $PID
        sleep 3
    fi
    rm "../../$PID_FILE"
fi

# ===== 새 앱 실행 =====
echo ">>> 새 앱 실행: $JAR_NAME"
nohup java -jar $JAR_NAME > "../../$LOG_FILE" 2>&1 &

# ===== PID 저장 =====
echo $! > "../../$PID_FILE"

# ===== 배포 성공 여부 분기 =====
if [ $? -eq 0 ]; then
    echo ""
    echo "===== 배포 성공! 🎉 고양이 축하 중 🎉"
    cat << "EOF"
 /\_/\
( o.o )
 > ^ <
EOF
else
    echo ""
    echo "===== 배포 실패... 😿 위로 고양이 등장 ====="
    cat << "EOF"
 /\_/\
( T.T )
 > ^ <
EOF
fi
