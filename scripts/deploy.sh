#!/bin/bash

# 스크립트 위치를 기준으로 프로젝트 루트 경로를 계산
# 이 스크립트 파일의 절대 경로
SCRIPT_PATH="$(readlink -f "$0")"
# 스크립트 디렉터리 (ex. /home/ubuntu/spring-roomescape-payment/scripts)
SCRIPT_DIR="$(dirname "$SCRIPT_PATH")"
# 프로젝트 루트 디렉터리 (ex. /home/ubuntu/spring-roomescape-payment)
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Java 설치 여부 확인 함수
is_java_installed() {
  if command -v java >/dev/null 2>&1; then
    return 0
  else
    return 1
  fi
}

# Java 설치 함수
install_java() {
  echo "Java 설치를 시도합니다..."
  wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add -
  sudo add-apt-repository 'deb https://apt.corretto.aws stable main'
  sudo apt-get update
  sudo apt-get install -y java-21-amazon-corretto-jdk
}

# 로컬 브랜치가 원격 최신 커밋과 같은지 확인하는 함수
# 인자: 브랜치 이름 (예: step2)
is_up_to_date_with_remote() {
  local branch="$1"

  # 원격 저장소 정보만 갱신
  git fetch origin "$branch" >/dev/null 2>&1

  # 로컬 HEAD와 origin/<branch> 해시 비교
  local LOCAL_HASH
  local REMOTE_HASH
  LOCAL_HASH="$(git rev-parse @)"
  REMOTE_HASH="$(git rev-parse "origin/$branch")"

  if [ "$LOCAL_HASH" = "$REMOTE_HASH" ]; then
    return 0 # 최신
  else
    return 1 # 업데이트 필요
  fi
}

main() {
  echo "=== Java 설치 여부 체크 ==="
  if ! is_java_installed; then
    echo "→ Java가 설치되어 있지 않습니다."
    echo "→ Java 설치 시작..."
    install_java

    # 설치 후 재확인
    if is_java_installed; then
      echo "→ Java 설치에 성공했습니다."
      java -version
    else
      echo "→ Java 설치에 실패했습니다." >&2
      exit 1
    fi
  else
    echo "→ Java가 이미 설치되어 있습니다."
    java -version
  fi

  echo ""
  echo "=== Git 프로젝트 세팅 시작 ==="
  # 스크립트 위치 기준으로 프로젝트 루트로 이동
  cd "$PROJECT_DIR" || { echo "프로젝트 디렉터리($PROJECT_DIR)로 이동 실패"; exit 1; }

  # 이미 clone된 상태이므로 별도 clone 로직 제거
  # step2 브랜치 체크아웃
  echo "→ 'step2' 브랜치 체크아웃"
  git checkout step2

  # 원격 최신 커밋인지 확인하여 필요 시 pull
  if is_up_to_date_with_remote "step2"; then
    echo "→ 로컬이 이미 origin/step2의 최신 커밋과 동일합니다. git pull 생략"
  else
    echo "→ 로컬이 origin/step2보다 뒤처져 있습니다. git pull 실행..."
    git pull origin step2
  fi

  echo ""
  echo "=== Gradle 빌드 시작 ==="
  ./gradlew bootJar
  echo "→ Gradle 빌드 완료"

  # 빌드 결과물이 위치한 디렉터리로 이동
  cd "$PROJECT_DIR/build/libs" || { echo "빌드 결과 디렉터리로 이동 실패"; exit 1; }

  echo ""
  echo "=== Spring 서버 실행 시작 ==="
  java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar
}

main
