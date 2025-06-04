#!/bin/bash

echo "==== 1. 최신 코드 가져오는 중... ===="
git pull origin step2

echo "==== 2. 도커 컴포즈 실행 (docker compose up -d) ===="
docker compose -f docker/docker-compose.yml up -d

echo "==== 3. 실행 중인 Spring 애플리케이션 종료 (중복 방지) ===="
pkill -f 'spring-roomescape-payment-0.0.1-SNAPSHOT.jar' || true

echo "==== 4. 애플리케이션 빌드 (./gradlew bootJar) ===="
./gradlew bootJar

echo "==== 5. Spring Boot 앱 실행 (nohup으로 백그라운드 실행) ===="
cd build/libs
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "==== 6. 배포 완료! ===="
