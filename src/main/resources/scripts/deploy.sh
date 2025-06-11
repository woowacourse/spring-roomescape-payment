#!/bin/bash

PROJECT_NAME=spring-roomescape-payment
JAR_NAME=spring-roomescape-payment-0.0.1-SNAPSHOT.jar
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

echo "> Move to project directory"
cd "$SCRIPT_DIR/$PROJECT_NAME" || exit 1

echo "> git pull"
git pull

echo "> project build"
./gradlew clean bootJar
if [ $? -ne 0 ]; then
  echo "> Build failed. Exiting."
  exit 1
fi

echo "> Kill existing application process"
CURRENT_PID=$(pgrep -f $JAR_NAME)
if [ -n "$CURRENT_PID" ]; then
  echo "> Kill process: $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> Starting application"

cd build/libs || exit 1

LOG_DIR=logs
mkdir -p $LOG_DIR
LOG_FILE=$LOG_DIR/app.log

nohup java -jar $JAR_NAME --spring.profiles.active=prod --server.port=8080 --server.address=0.0.0.0 > $LOG_FILE 2>&1 &

echo "> Application started with PID: $!"
