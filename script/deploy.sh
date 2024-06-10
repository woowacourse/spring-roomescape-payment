#!/bin/sh

RED="\e[1;31m"
GREEN="\e[1;34m"
ENDCOLOR="\e[0m"
PROJECT_NAME=spring-roomescape-payment

echo "\\n==== Start Deploy ====\\n"

echo "${GREEN}> Git Pull${ENDCOLOR}"

cd $PROJECT_NAME
git pull origin step2

echo "${GREEN}> Build Project${ENDCOLOR}"

./gradlew bootJar

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "\\n> Running Project PID : $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "> NONE\\n"
else
        echo "${RED}> kill -9 ${CURRENT_PID}${ENDCOLOR}\\n"
        kill -9 $CURRENT_PID
        sleep 5
fi
echo "${GREEN}> Deploy New Project${ENDCOLOR}\\n"
JAR_NAME=$(ls -tr build/libs/*.jar | tail -n 1)

echo "> jar Name : $JAR_NAME\\n"

nohup java -jar -Dspring.profiles.active=prod $JAR_NAME &
