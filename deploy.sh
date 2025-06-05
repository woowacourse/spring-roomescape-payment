
#!/bin/bash
# deploy.sh

set -e  # 오류 발생 시 스크립트 중단
REPO_URL="https://github.com/changuii/spring-roomescape-payment.git"
REPO_DIR="spring-roomescape-payment"
BUILD_DIR="$REPO_DIR/build/libs"
PORT_A=8080
PORT_B=8090
BRANCH="step2"
JAR_FILE_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"

echo "[INFO] 배포 스크립트 시작"

function is_port_in_use {
    lsof -i :$1 -t
}

function update_port_forwarding {
    FROM=$1
    TO=$2
    echo "[INFO] 포트 포워딩 설정: $FROM → $TO"
    sudo iptables -t nat -R PREROUTING 1 -p tcp --dport $FROM -j REDIRECT --to-port $TO 2>/dev/null || \
    sudo iptables -t nat -A PREROUTING -p tcp --dport $FROM -j REDIRECT --to-port $TO
}

function kill_process_on_port {
    PID=$(lsof -i :$1 -t)
    if [ ! -z "$PID" ]; then
        echo "[INFO] 포트 $1에서 실행 중인 프로세스 종료 (PID: $PID)"
        kill -9 $PID
    else
        echo "[INFO] 포트 $1에서 실행 중인 프로세스 없음"
    fi
}

if [ -d "$REPO_DIR" ]; then
  echo "[INFO] 기존 프로젝트 디렉토리 존재 - git pull 수행"
  cd $REPO_DIR
  git checkout $BRANCH
  git pull origin $BRANCH
else
  echo "[INFO] 프로젝트 디렉토리 없음 - git clone 수행"
  git clone --branch $BRANCH --depth=1 $REPO_URL
  cd $REPO_DIR
fi

echo "[INFO] 프로젝트 빌드 시작"
#cd $REPO_DIR
./gradlew build  || { echo "[ERROR] 빌드 실패! 스크립트 종료"; exit 1; }

if is_port_in_use $PORT_A > /dev/null; then
    NEXT_PORT=$PORT_B
    OLD_PORT=$PORT_A
else
    NEXT_PORT=$PORT_A
    OLD_PORT=$PORT_B
fi

echo "[INFO] 다음 포트: $NEXT_PORT, 이전 포트: $OLD_PORT"

JAR_FILE=$(find build/libs -name $JAR_FILE_NAME | head -n 1)
echo "[INFO] 애플리케이션 실행: $JAR_FILE (port: $NEXT_PORT)"
nohup java -jar $JAR_FILE --server.port=$NEXT_PORT > app_$NEXT_PORT.log 2>&1 &
tail -n 20 app_$NEXT_PORT.log

sleep 15
update_port_forwarding $OLD_PORT $NEXT_PORT
kill_process_on_port $OLD_PORT

echo "[INFO] 배포 완료"
cat app_$NEXT_PORT.log
