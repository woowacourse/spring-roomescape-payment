#!/bin/bash

BRANCH_NAME="step2"
REMOTE_NAME="origin"
PORT_NUMBER=8080
set -e

# 최신 커밋 불러오기
cd ~/spring-roomescape-payment;
git checkout $BRANCH_NAME;
git pull $REMOTE_NAME $BRANCH_NAME;
echo "최신 커밋 불러오기 완료";

# 빌드
./gradlew bootJar;
echo "빌드 완료";

# 기존 애플리케이션 종료
PID=$(lsof -t -i:$PORT_NUMBER)
if [ -n "$PID" ]; then
  echo "PID $PID 에 SIGTERM 시그널 전송..."
  kill "$PID"
  # 10초 대기하며 종료 확인
  for i in {1..10}; do
    if ! kill -0 "$PID" 2>/dev/null; then
      echo "PID $PID 성공적으로 종료됨."
      break
    fi
    sleep 1
  done
  # 여전히 실행 중이면 강제 종료
  if kill -0 "$PID" 2>/dev/null; then
    echo "PID $PID 강제 종료 (kill -9) 시도..."
    kill -9 "$PID"
  fi
fi

# 스프링 애플리케이션 실행
nohup java -jar "$(find build/libs -name "*.jar" | grep -v plain | head -1)" &
echo "애플리케이션 시작 완료";

echo "서버와의 SSH 연결 종료됨."
echo "--- 배포 완료 ---"