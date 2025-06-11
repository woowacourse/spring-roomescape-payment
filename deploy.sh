#!/bin/bash

# 1. 기존 프로세스 종료 시도
target_pid=$(lsof -i :8080 -t)
if [ -n "$target_pid" ]; then
    echo "Stopping existing process (PID=$target_pid)..."
    kill -15 $target_pid
    sleep 5
    if ps -p $target_pid > /dev/null; then
        echo "Graceful shutdown failed. Force killing..."
        kill -9 $target_pid
    fi
fi

# 2. 최신 코드 가져오기
echo "Fetching latest code..."
git pull

# 3. 빌드
echo "Building jar..."
./gradlew build

# 4. 실행
echo "Starting application..."
nohup java -jar ./build/libs/spring-roomescape-payment-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
