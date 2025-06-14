#!/bin/bash


# 1. 스크립트 설정 및 변수 정의
PROJECT_DIR="/home/eoehd1ek/spring-roomescape-payment"
REPOSITORY_URL="https://github.com/eoehd1ek/spring-roomescape-payment.git"
BRANCH_NAME="step2"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
APP_PORT="8080"

LOG_DIR="/home/eoehd1ek/logs"
DEPLOY_LOG="$LOG_DIR/deploy.log"
APP_LOG="$LOG_DIR/application.log"

# 배포 스크립트 로그 파일 초기화
mkdir -p "$LOG_DIR"
echo "1. 배포 시작: $(date)" > "$DEPLOY_LOG" 2>&1
# set -e


# 2. 8080 포트 점유 프로세스 종료
echo "2. 8080 포트 점유 프로세스 종료 시작" | tee -a "$DEPLOY_LOG"
TARGET_PID=$(lsof -i :$APP_PORT -t 2>/dev/null)

if [ -z "$TARGET_PID" ]; then
    echo "  - 8080 포트 점유 프로세스가 없습니다." | tee -a "$DEPLOY_LOG"
else
    echo "  - 8080 포트 점유 프로세스 (PID: $TARGET_PID) 종료 시작" | tee -a "$DEPLOY_LOG"
    kill -9 $TARGET_PID
    echo "  - 8080 포트 점유 프로세스 (PID: $TARGET_PID) 종료 완료" | tee -a "$DEPLOY_LOG"
fi
echo "2. 8080 포트 점유 프로세스 종료 끝" | tee -a "$DEPLOY_LOG"


# 3. 최신 소스 코드 가져오기 (Git Clone or Pull)
echo "3. 최신 소스 코드 가져오기 시작" | tee -a "$DEPLOY_LOG"
if [ -d "$PROJECT_DIR" ]; then
    echo "  - git pull 수행 시작" | tee -a "$DEPLOY_LOG"
    cd "$PROJECT_DIR"
    git fetch origin "$BRANCH_NAME" | tee -a "$DEPLOY_LOG"
    git pull origin "$BRANCH_NAME" | tee -a "$DEPLOY_LOG"
    echo "  - git pull 수행 종료" | tee -a "$DEPLOY_LOG"
else
    echo "  - git clone 수행 시작" | tee -a "$DEPLOY_LOG"
    git clone "$REPOSITORY_URL" "$PROJECT_DIR" | tee -a "$DEPLOY_LOG"
    cd "$PROJECT_DIR"
    echo "  - git clone 수행 종료" | tee -a "$DEPLOY_LOG"
fi

# step2 브랜치 전환
echo "  - 브랜치 '$BRANCH_NAME' 전환 시작" | tee -a "$DEPLOY_LOG"
git checkout "$BRANCH_NAME" | tee -a "$DEPLOY_LOG"
echo "  - 브랜치 '$BRANCH_NAME' 전환 종료" | tee -a "$DEPLOY_LOG"
echo "3. 최신 소스 코드 가져오기 끝" | tee -a "$DEPLOY_LOG"


# 4. spring 프로젝트 Gradle 빌드
echo "4. Gradle 빌드 시작" | tee -a "$DEPLOY_LOG"
cd "$PROJECT_DIR"
chmod +x gradlew
./gradlew bootJar >> "$DEPLOY_LOG" 2>&1
BUILD_STATUS=$? # 이전 명령어의 종료 코드 저장.
echo "  - Gradle 빌드 상태 코드: $BUILD_STATUS" | tee -a "$DEPLOY_LOG"

if [ "$BUILD_STATUS" -ne 0 ]; then
    echo "  - [오류] Gradle 빌드 실패!" | tee -a "$DEPLOY_LOG"
    echo "----- 배포 실패: $(date) -----" | tee -a "$DEPLOY_LOG"
    exit 1 # 스크립트 종료
fi
echo "  - Gradle 빌드 완료." | tee -a "$DEPLOY_LOG"
echo "4. Gradle 빌드 끝" | tee -a "$DEPLOY_LOG"


# 5. 애플리케이션 실행
echo "5. 빌드 파일 실행 시작" | tee -a "$DEPLOY_LOG"
# 빌드된 JAR 파일의 경로 설정
JAR_PATH="$PROJECT_DIR/build/libs/$JAR_NAME"
echo "  - 빌드된 JAR 파일 경로: $JAR_PATH" | tee -a "$DEPLOY_LOG"
if [ ! -f "$JAR_PATH" ]; then
    echo "  - [오류] JAR 파일 ($JAR_PATH)을 찾을 수 없습니다. 빌드가 제대로 되지 않았을 수 있습니다." | tee -a "$DEPLOY_LOG"
    echo "----- 배포 실패: $(date) -----" | tee -a "$DEPLOY_LOG"
    exit 1
fi

# nohup: 터미널이 끊어져도 백그라운드에서 계속 실행
# java -jar: Spring Boot JAR 파일 실행 명령어
# > "$APP_LOG" 2>&1: 표준 출력과 표준 오류를 모두 지정된 로그 파일로 리다이렉트
# &: 백그라운드에서 실행
nohup java -jar "$JAR_PATH" > "$APP_LOG" 2>&1 &
echo "  - 빌드된 JAR 파일 실행" | tee -a "$DEPLOY_LOG"


# 6. 배포 완료
echo "--- 배포 성공: $(date) ---" | tee -a "$DEPLOY_LOG"
echo "배포 스크립트 실행 완료. 애플리케이션 로그는 '$APP_LOG'에서 확인하세요." | tee -a "$DEPLOY_LOG"
echo "배포 결과 로그는 '$DEPLOY_LOG'에서 확인하세요." | tee -a "$DEPLOY_LOG"

# 스크립트 종료
exit 0
