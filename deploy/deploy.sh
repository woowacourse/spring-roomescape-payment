#!/bin/bash

APP_NAME="spring-roomescape"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
PROJECT_PATH="$HOME/spring-roomescape-payment"
PROFILE=${1:-prod}
PORT=8080

# 로그 색상 코드
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%H:%M:%S') - $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%H:%M:%S') - $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $(date '+%H:%M:%S') - $1"
}

# 스크립트가 실패하면 자동으로 중단되도록 설정
set -e

close_existing_application() {
    log_info "========================================="
    log_info "기존 애플리케이션을 종료합니다..."

    PID=$(lsof -t -i:$PORT 2>/dev/null || echo "")

    if [ -n "$PID" ]; then
        log_info "PID $PID 프로세스를 종료합니다"

        kill -15 $PID

        sleep 3

        if kill -0 $PID 2>/dev/null; then
            log_warn "정상 종료되지 않아 강제 종료합니다"
            kill -9 $PID
        fi

        log_info "애플리케이션 종료 완료"
    else
        log_info "실행 중인 애플리케이션이 없습니다"
    fi
}

build_project() {
    log_info "========================================="
    log_info "프로젝트 디렉토리로 이동: $PROJECT_PATH"

    cd "$PROJECT_PATH" || {
        log_error "프로젝트 디렉토리를 찾을 수 없습니다: $PROJECT_PATH"
        exit 1
    }

    log_info "프로젝트를 최신화합니다..."

    git checkout step2
    git fetch
    git pull

    log_info "프로젝트를 빌드합니다..."

    ./gradlew clean build -x test

    JAR_PATH="build/libs/$JAR_NAME"
    if [ ! -f "$JAR_PATH" ]; then
        log_error "빌드된 JAR 파일을 찾을 수 없습니다: $JAR_PATH"
        exit 1
    fi

    log_info "빌드 완료: $JAR_PATH"
}

start_application() {
    log_info "========================================="
    log_info "새 애플리케이션을 시작합니다..."

    LOG_DIR="$HOME/logs"
    mkdir -p "$LOG_DIR"

    APP_LOG_FILE="$LOG_DIR/app_${APP_NAME}_$(date +%Y%m%d).log"
    ERR_LOG_FILE="$LOG_DIR/err_${APP_NAME}_$(date +%Y%m%d).log"

    nohup java \
        -Dspring.profiles.active=$PROFILE \
        -Dserver.port=$PORT \
        -Xmx1024m \
        -Xms512m \
        -jar "$JAR_PATH" \
        > "$APP_LOG_FILE" \
        2> "$ERR_LOG_FILE" &

    NEW_PID=$!
    log_info "애플리케이션이 시작되었습니다. PID: $NEW_PID"
    log_info "일반 로그 파일: $APP_LOG_FILE"
    log_info "에러 로그 파일: $ERR_LOG_FILE"

    log_info "실행을 위한 10초 대기..."
    sleep 10
}

check_application_status() {
    log_info "========================================="
    log_info "헬스 체크 시작..."

    for i in {1..10}; do
      HEALTH_STATUS=$(curl -s "http://localhost:$PORT/actuator/health" | jq -r '.status' 2>/dev/null || echo "")
      if [ "$HEALTH_STATUS" = "UP" ]; then
        log_info "✅ 애플리케이션 정상 작동"
        return 0
      fi

      log_warn "[$i/10] 대기 중..."
      sleep 3
    done

    log_error "❌ 애플리케이션 시작 실패"
    return 1
}

main() {
    log_info "========================================="
    log_info "Spring Boot 애플리케이션 배포를 시작합니다"

    close_existing_application

    build_project

    start_application

    if check_application_status; then
        log_info "배포 완료! 🎉"
    else
        log_warn "헬스체크 실패했지만 애플리케이션은 실행 중입니다."
    fi

    log_info "========================================="
    log_info "애플리케이션 URL: http://localhost:$PORT"
    log_info "일반 로그 확인: tail -f $APP_LOG_FILE"
    log_info "에러 로그 확인: tail -f $ERR_LOG_FILE"
    log_info "프로세스 종료: kill $NEW_PID"
    log_info "========================================="
}

main
