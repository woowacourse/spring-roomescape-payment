#!/bin/bash

set -e

BRANCH="step2"
DOCKER_COMPOSE_FILE="docker/docker-compose.yml"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
BUILD_DIR="build/libs"
LOG_FILE="app.log"

echo "==== 1. 최신 코드 가져오는 중... ===="
git pull origin $BRANCH

echo "==== 2. 도커 컴포즈 실행 (docker compose up -d) ===="
sudo docker-compose -f $DOCKER_COMPOSE_FILE up -d

echo "==== 3. 실행 중인 Spring 애플리케이션 종료 (중복 방지) ===="
pkill -f $JAR_NAME || true

echo "==== 4. 애플리케이션 빌드 ===="
./gradlew bootJar

echo "==== 5. Spring Boot 앱 실행 (nohup으로 백그라운드 실행) ===="
nohup java -jar $BUILD_DIR/$JAR_NAME > $LOG_FILE 2>&1 &

echo "==== 6. 배포 완료! ===="
