#!/bin/bash

SCRIPT_PATH="$(readlink -f "$0")"
SCRIPT_DIR="$(dirname "$SCRIPT_PATH")"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

is_java_installed() {
  if command -v java >/dev/null 2>&1; then
    return 0
  else
    return 1
  fi
}


install_java() {
  echo "install java..."
  wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add -
  sudo add-apt-repository 'deb https://apt.corretto.aws stable main'
  sudo apt-get update
  sudo apt-get install -y java-21-amazon-corretto-jdk
}

is_up_to_date_with_remote() {
  local branch="$1"

  git fetch origin "$branch" >/dev/null 2>&1

  local LOCAL_HASH
  local REMOTE_HASH
  LOCAL_HASH="$(git rev-parse @)"
  REMOTE_HASH="$(git rev-parse "origin/$branch")"

  if [ "$LOCAL_HASH" = "$REMOTE_HASH" ]; then
    return 0
  else
    return 1
  fi
}

start_server() {
  PORT=8080

  PID=$(lsof -t -i :$PORT)

  if [ -n "$PID" ]; then
    kill -9 "$PID"
    echo "using port $PORT : PID=$PID → process exit"
  fi

  nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &
  echo "Spring server process PID: $!"
}

main() {
  # java 세팅
  echo "check install java..."
  if ! is_java_installed; then
    echo "not found java..."
    echo "install java..."
    install_java

    if is_java_installed; then
      echo "java install complete"
      java -version
    else
      echo "fail install java..."
      exit 1
    fi
  else
    java -version
  fi

  # git 세팅
  echo ""
  echo "setting git project..."
  cd "$PROJECT_DIR" || { echo "$PROJECT_DIR error"; exit 1; }

  # step2 브랜치 체크아웃
  echo "checkout step2"
  git checkout step2

  # 원격 최신 커밋인지 확인하여 필요 시 pull
  if ! is_up_to_date_with_remote "step2"; then
    echo "git pull ..."
    git pull origin step2
  fi

  # 빌드 시작
  echo ""
  echo "build start..."
  ./gradlew bootJar

  # 빌드 결과물이 위치한 디렉터리로 이동
  cd "$PROJECT_DIR/build/libs" || { echo "error"; exit 1; }

  # Spring 서버 백그라운드 실행
  echo ""
  echo "start spring server..."
  start_server
}

main
