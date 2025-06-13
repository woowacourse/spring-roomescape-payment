#!/bin/bash

set -e

APP_NAME="spring-roomescape-payment"
JAR_NAME="${APP_NAME}-0.0.1-SNAPSHOT.jar"
REPO_URL="https://github.com/kysub99/spring-roomescape-payment.git"
BRANCH="step2"
LOG_DIR="/var/log/deploy"
LOG_FILE="$LOG_DIR/deploy.log"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# 색상 정의
NC="\033[0m"
RED="\033[0;31m"
YELLOW="\033[0;33m"
GREEN="\033[0;32m"
CYAN="\033[0;36m"

# 로그 디렉토리 생성
sudo mkdir -p $LOG_DIR
sudo chown $USER:$USER $LOG_DIR

# 로그 함수들
log_debug() {
    echo -e "${CYAN}[$TIMESTAMP] [DEBUG] $1${NC}" | tee -a $LOG_FILE
}

log_info() {
    echo -e "${GREEN}[$TIMESTAMP] [INFO] $1${NC}" | tee -a $LOG_FILE
}

log_warn() {
    echo -e "${YELLOW}[$TIMESTAMP] [WARN] $1${NC}" | tee -a $LOG_FILE
}

log_error() {
    echo -e "${RED}[$TIMESTAMP] [ERROR] $1${NC}" | tee -a $LOG_FILE
}

# 에러 트래킹 함수
error_exit() {
    log_error "배포 실패: $1"
    log_error "스크립트 종료 - 라인: $2"
    exit 1
}

# 에러 트랩 설정
trap 'error_exit "예상치 못한 오류 발생" $LINENO' ERR

log_info "=== 배포 시작 ==="
log_info "애플리케이션: $APP_NAME"
log_info "사용자: $(whoami)"
log_info "호스트: $(hostname)"

# Java 설치 확인
log_info "Java 설치 확인 중..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 21 ]; then
        log_info "Java $JAVA_VERSION 이미 설치됨 - 설치 건너뛰기"
    else
        log_warn "Java $JAVA_VERSION 버전이 낮음 - Java 21 설치 필요"
        install_java21=true
    fi
else
    log_info "Java가 설치되지 않음 - Java 21 설치 필요"
    install_java21=true
fi

if [ "$install_java21" = true ]; then
    log_info "Java 21 설치 중..."
    wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add - 2>>$LOG_FILE
    sudo add-apt-repository 'deb https://apt.corretto.aws stable main' -y 2>>$LOG_FILE
    sudo apt-get update 2>>$LOG_FILE
    sudo apt-get install -y java-21-amazon-corretto-jdk 2>>$LOG_FILE
    log_info "Java 21 설치 완료"
fi

# 기존 프로세스 확인 및 종료
log_info "기존 프로세스 확인 중..."
if pgrep -f "$JAR_NAME" > /dev/null; then
    log_warn "기존 프로세스 발견 - 종료 중..."
    pkill -f "$JAR_NAME" || log_warn "프로세스 종료 실패"
    sleep 3
    if pgrep -f "$JAR_NAME" > /dev/null; then
        log_error "프로세스가 여전히 실행 중"
        pkill -9 -f "$JAR_NAME" || true
        log_warn "강제 종료 수행"
    fi
    log_info "기존 프로세스 종료 완료"
else
    log_info "실행 중인 프로세스 없음"
fi

# 소스 코드 다운로드 및 빌드
log_info "소스 코드 다운로드 시작..."
cd /tmp
if [ -d "$APP_NAME" ]; then
    log_debug "기존 소스 디렉토리 삭제"
    rm -rf $APP_NAME
fi

log_debug "Git clone 실행: $REPO_URL (브랜치: $BRANCH)"
if git clone -b $BRANCH $REPO_URL 2>>$LOG_FILE; then
    log_info "소스 코드 다운로드 완료 ($BRANCH 브랜치)"
else
    error_exit "Git clone 실패" $LINENO
fi

cd $APP_NAME
log_info "애플리케이션 빌드 시작..."
if ./gradlew clean bootJar 2>>$LOG_FILE; then
    log_info "빌드 성공"
else
    error_exit "빌드 실패" $LINENO
fi

# JAR 파일 존재 확인
if [ ! -f "build/libs/$JAR_NAME" ]; then
    error_exit "JAR 파일을 찾을 수 없음: build/libs/$JAR_NAME" $LINENO
fi

log_debug "JAR 파일 크기: $(du -h build/libs/$JAR_NAME | cut -f1)"

# JAR 파일 배포
log_info "JAR 파일 배포 중..."
cd build/libs
if cp $JAR_NAME ~/; then
    log_info "JAR 파일 배포 완료"
else
    error_exit "JAR 파일 복사 실패" $LINENO
fi

# 애플리케이션 시작
log_info "애플리케이션 시작 중..."
cd ~

# 애플리케이션 로그 디렉토리 생성
APP_LOG_DIR="/var/log/spring-app"
sudo mkdir -p $APP_LOG_DIR
sudo chown $USER:$USER $APP_LOG_DIR

# 애플리케이션 시작 (상세 로그와 함께)
log_debug "실행 명령: nohup java -jar $JAR_NAME"
if nohup java -jar $JAR_NAME > $APP_LOG_DIR/application.log 2>&1 &
then
    APP_PID=$!
    echo $APP_PID > app.pid
    log_info "애플리케이션 시작 완료 (PID: $APP_PID)"
else
    error_exit "애플리케이션 시작 실패" $LINENO
fi

# 헬스체크
log_info "애플리케이션 헬스체크 중..."
sleep 10

if ps -p $APP_PID > /dev/null; then
    log_info "애플리케이션이 정상적으로 실행 중"

    # 포트 확인
    if netstat -tlnp 2>/dev/null | grep :8080 > /dev/null; then
        log_info "포트 8080에서 서비스 확인됨"
    else
        log_warn "포트 8080에서 서비스를 확인할 수 없음"
    fi
else
    log_error "애플리케이션 프로세스가 종료됨"
    log_error "애플리케이션 로그:"
    tail -20 $APP_LOG_DIR/application.log | while read line; do
        log_error "APP_LOG: $line"
    done
    exit 1
fi

log_info "=== 배포 완료 ==="
log_info "애플리케이션 로그: tail -f $APP_LOG_DIR/application.log"
log_info "배포 로그: tail -f $LOG_FILE"
log_info "프로세스 ID: $APP_PID"

# 배포 통계
END_TIME=$(date '+%Y-%m-%d %H:%M:%S')
log_info "배포 시작: $TIMESTAMP"
log_info "배포 완료: $END_TIME"
log_info "배포 스크립트 실행 완료"
