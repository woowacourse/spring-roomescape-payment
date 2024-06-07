#!/bin/bash

PROJECT_NAME=spring-roomescape-payment
JAR_DIRECTORY=build/libs
LOG_DIRECTORY=/home/ubuntu/log

echo ">>> Git pull"
git pull origin step2

echo ">>> Build with Gradle"
./gradlew clean
./gradlew bootJar

echo ">>> Stop Running server"
RUNNUNG_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)
if [ -n "$RUNNUNG_PID" ]; then
  echo ">>>>> Kill process."
  kill -15 $RUNNUNG_PID
  else
    echo ">>>>> Process does not exist."
fi

echo ">>> Deploy"
[ ! -d "$LOG_DIRECTORY" ] && mkdir -p "$LOG_DIRECTORY"
cd ${JAR_DIRECTORY}
JAR_FILE=$(find . -maxdepth 1 -name "*.jar" | head -n 1)

if [ -n "$JAR_FILE" ]; then
  nohup java -Dspring.profiles.active=deploy -jar "$JAR_FILE" > "$LOG_DIRECTORY/room_escape_log.out" 2>&1 &
else
  echo ">>>>> No .jar files found in the current directory."
fi
