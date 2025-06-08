#!/bin/bash

# 배포 스크립트 - Spring Boot 애플리케이션
# 사용법: ./deploy.sh

# ========================================
# 설정 변수들
# ========================================
APP_NAME="spring-roomescape"                            # 애플리케이션 이름
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar" # 실행할 JAR 파일명
PROJECT_PATH="/home/ubuntu/spring-roomescape-payment"   # 프로젝트 경로
LOG_DIR="/home/ubuntu/logs"                             # 로그 디렉토리
BACKUP_DIR="/home/ubuntu/backup"                        # 백업 디렉토리
PORT=8080                                               # 서비스 포트
PROFILE=${1:-prod}

# 색상 코드 (로그 출력용)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ========================================
# 함수 정의
# ========================================

# 환경변수 로딩
load_env_file() {
  ENV_FILE="$(dirname "$0")/.env"

  if [ -f "$ENV_FILE" ]; then
    log_info ".env 파일을 로드합니다: $ENV_FILE"
    set -a
    source "$ENV_FILE"
    set +a
  else
    log_warn ".env 파일이 존재하지 않습니다. 시스템 환경변수를 사용합니다."
  fi
}

# 환경변수 검증
validate_env() {
  REQUIRED_VARS=("JWT_TOKEN_SECRET_KEY" "TOSS_PAYMENT_SECRET_KEY")

  log_info "필요한 환경변수 설정 여부를 확인합니다..."

  for var in "${REQUIRED_VARS[@]}"; do
    value="${!var}"
    if [ -z "$value" ]; then
      log_error "❌ 환경변수 $var 가 설정되지 않았습니다."
      MISSING=true
    else
      log_info "✅ $var = ${value:0:10}..." # 보안상 앞 10자만 출력
    fi
  done

  if [ "$MISSING" = true ]; then
    log_error "필수 환경변수가 누락되었습니다. 배포를 중단합니다."
    exit 1
  fi
}

# 로그 출력 함수
log_info() {
  echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_warn() {
  echo -e "${YELLOW}[WARN]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
  echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# 디렉토리 생성 함수
create_directories() {
  log_info "필요한 디렉토리들을 생성합니다..."
  mkdir -p $LOG_DIR
  mkdir -p $BACKUP_DIR
  mkdir -p $PROJECT_PATH
}

# 기존 프로세스 종료 함수
stop_application() {
  log_info "기존 애플리케이션을 종료합니다..."

  # 포트로 실행 중인 프로세스 찾기
  PID=$(lsof -t -i:$PORT)

  if [ ! -z "$PID" ]; then
    log_info "PID $PID 프로세스를 종료합니다..."
    kill -15 $PID # SIGTERM으로 graceful shutdown
    sleep 5

    # 아직 살아있으면 강제 종료
    if kill -0 $PID 2>/dev/null; then
      log_warn "Graceful shutdown 실패. 강제 종료합니다..."
      kill -9 $PID
    fi

    log_info "애플리케이션이 종료되었습니다."
  else
    log_info "실행 중인 애플리케이션이 없습니다."
  fi
}

# 이전 빌드 백업 함수
backup_previous_build() {
  if [ -f "$PROJECT_PATH/build/libs/$JAR_NAME" ]; then
    log_info "이전 빌드를 백업합니다..."
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    cp "$PROJECT_PATH/build/libs/$JAR_NAME" "$BACKUP_DIR/${JAR_NAME}_$TIMESTAMP"
    log_info "백업 완료: ${JAR_NAME}_$TIMESTAMP"
  fi
}

# 프로젝트 빌드 함수
build_application() {
  log_info "프로젝트를 빌드합니다..."

  cd $PROJECT_PATH

  # 기존 빌드 파일 정리
  log_info "기존 빌드 파일을 정리합니다..."
  ./gradlew clean

  # 새로운 빌드 실행
  log_info "새로운 빌드를 시작합니다..."
  ./gradlew build -x test # 테스트 제외하고 빌드

  if [ $? -eq 0 ]; then
    log_info "빌드가 성공했습니다."
  else
    log_error "빌드가 실패했습니다."
    exit 1
  fi
}

# 애플리케이션 실행 함수
start_application() {
  log_info "애플리케이션을 시작합니다..."

  cd $PROJECT_PATH

  # JAR 파일 경로 설정
  JAR_PATH="build/libs/$JAR_NAME"

  if [ ! -f "$JAR_PATH" ]; then
    log_error "JAR 파일을 찾을 수 없습니다: $JAR_PATH"
    exit 1
  fi

  # 로그 파일명 설정 (날짜별로 구분)
  DATE=$(date +%Y%m%d)
  APP_LOG="$LOG_DIR/${APP_NAME}_${DATE}.log"
  ERROR_LOG="$LOG_DIR/${APP_NAME}_error_${DATE}.log"

  # nohup으로 백그라운드 실행
  # 표준 출력과 에러를 분리하여 저장
  nohup java -jar \
    -Dspring.profiles.active=$PROFILE \
    -Dserver.port=$PORT \
    -Xmx1024m \
    -Xms512m \
    "$JAR_PATH"

  # 프로세스 ID 저장
  echo $! >"$LOG_DIR/${APP_NAME}.pid"

  log_info "애플리케이션이 시작되었습니다. PID: $!"
  log_info "애플리케이션 로그: $APP_LOG"
  log_info "에러 로그: $ERROR_LOG"
}

check_application_status() {
  log_info "애플리케이션 상태를 확인합니다..."

  local max_attempts=30
  local attempt=1
  local wait_time=10

  # 애플리케이션 시작 대기
  log_info "애플리케이션 시작을 위해 ${wait_time}초 대기합니다..."
  sleep $wait_time

  # 포트가 열릴 때까지 대기 (최대 30초)
  log_info "포트 $PORT 가 열릴 때까지 대기합니다..."
  for i in {1..10}; do
    if lsof -i:$PORT >/dev/null 2>&1; then
      log_info "✅ 포트 $PORT 가 열렸습니다."
      break
    fi
    log_warn "⏳ 포트 $PORT 가 아직 열리지 않았습니다. 재시도 중 ($i/10)"
    sleep 3
  done

  while [ $attempt -le $max_attempts ]; do
    log_info "Health Check 시도 $attempt/$max_attempts"

    # HTTP Health Check
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
      "http://localhost:$PORT/actuator/health" \
      --connect-timeout 5 --max-time 10 2>/dev/null || echo "000")

    log_info "HTTP 상태 코드: $HTTP_STATUS"

    if [ "$HTTP_STATUS" = "200" ]; then
      # . 응답 내용 확인
      HEALTH_RESPONSE=$(curl -s "http://localhost:$PORT/actuator/health" \
        --connect-timeout 5 --max-time 10 2>/dev/null || echo "")

      if echo "$HEALTH_RESPONSE" | grep -q '"status":"UP"'; then
        log_info "✅ Health Check 성공!"
        log_info "응답: $HEALTH_RESPONSE"
        return 0
      else
        log_warn "응답에 UP 상태가 없습니다: $HEALTH_RESPONSE"
      fi
    elif [ "$HTTP_STATUS" = "000" ]; then
      log_warn "연결 실패 - 애플리케이션이 아직 시작 중일 수 있습니다."
    else
      log_warn "예상치 못한 HTTP 상태: $HTTP_STATUS"
    fi

    sleep 3
    ((attempt++))
  done

  # 실패 시 디버그 정보 출력
  log_error "❌ Health Check 최종 실패"
  log_error "디버그 정보:"

  # 포트 상태 확인
  if lsof -i:$PORT >/dev/null 2>&1; then
    log_error "포트 $PORT 사용 프로세스:"
    lsof -i:$PORT
  else
    log_error "포트 $PORT에 실행 중인 프로세스가 없습니다."
  fi

  # 최근 로그 확인
  APP_LOG="$LOG_DIR/${APP_NAME}_$(date +%Y%m%d).log"
  ERROR_LOG="$LOG_DIR/${APP_NAME}_error_$(date +%Y%m%d).log"

  if [ -f "$ERROR_LOG" ]; then
    log_error "최근 에러 로그 (마지막 20줄):"
    tail -20 "$ERROR_LOG"
  fi

  if [ -f "$APP_LOG" ]; then
    log_error "최근 애플리케이션 로그 (마지막 20줄):"
    tail -20 "$APP_LOG"
  fi

  return 1
}

# 로그 관리 함수
manage_logs() {
  log_info "오래된 로그 파일을 정리합니다..."

  # 7일 이상 된 로그 파일 삭제
  find $LOG_DIR -name "*.log" -mtime +7 -delete
  find $BACKUP_DIR -name "*.jar" -mtime +30 -delete

  log_info "로그 정리 완료"
}

# ========================================
# 메인 실행 부분
# ========================================

main() {
  log_info "========================================="
  log_info "Spring Boot 애플리케이션 배포를 시작합니다"
  log_info "========================================="

  # 0. 환경변수
  load_env_file
  validate_env

  # 1. 디렉토리 생성
  create_directories

  # 2. 기존 애플리케이션 종료
  stop_application

  # 3. 이전 빌드 백업
  backup_previous_build

  # 4. 프로젝트 빌드
  build_application

  # 5. 애플리케이션 시작
  start_application

  # 6. 상태 확인
  check_application_status

  # 7. 로그 관리
  manage_logs

  log_info "========================================="
  log_info "배포가 성공적으로 완료되었습니다! 🎉"
  log_info "========================================="
}

# 스크립트 실행
main "$@"
