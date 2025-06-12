#!/bin/bash

# 변수 설정

source .env

echo "> Git 프로젝트 클론 또는 pull"
if [ ! -d "$PROJECT_NAME" ]; then
  git clone $REPO_URL
  cd $PROJECT_NAME || exit
  git fetch origin
  git checkout $BRANCH_NAME
  git pull origin $BRANCH_NAME
else
  cd $PROJECT_NAME || exit
  git fetch origin
  git checkout $BRANCH_NAME
  git pull origin $BRANCH_NAME
fi

echo "> 브랜치 변경: $BRANCH_NAME"
git checkout $BRANCH_NAME

echo "> Gradle 빌드 (bootJar)"
TOSS_WIDGET_SECRET_KEY=$SECRET_KEY ./gradlew bootJar

cd build/libs || exit

echo "> 로그 디렉토리 생성: $LOG_DIR"
mkdir -p $LOG_DIR

echo "> 실행 중인 프로세스 종료"
CURRENT_PID=$(pgrep -f $JAR_NAME)
if [ -z "$CURRENT_PID" ]; then
  echo "> 실행 중인 프로세스 없음"
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 애플리케이션 실행"
nohup env TOSS_WIDGET_SECRET_KEY=$SECRET_KEY \
  java -jar $JAR_NAME > $LOG_DIR/nohup.out 2>&1 &

echo "> 배포 완료!"
