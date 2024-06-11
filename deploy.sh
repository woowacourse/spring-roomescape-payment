#! /bin/bash

PROJECT_PATH=/home/opc/wooteco/spring-roomescape-payment
PROJECT_NAME=spring-roomescape-payment
PROJECT_BUILD_PATH=build/libs

cd $PROJECT_PATH/$PROJECT_NAME

clear

printf "GitHub에서 프로젝트 pull\n"

git pull origin step2

printf "\n프로젝트 빌드 시작\n"

./gradlew bootJar

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}-.*.jar | head -n 1)

if [ -z "$CURRENT_PID" ]; then
	echo "구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
	echo "구동중인 애플리케이션을 종료했습니다. (pid: $CURRENT_PID)"
	kill -15 $CURRENT_PID
fi

printf "\nSpringBoot 환경변수 설정"

export ROOMESCAPE_MYSQL_DB_URL="..."
export ROOMESCAPE_MYSQL_USERNAME="..."
export ROOMESCAPE_MYSQL_PASSWORD="..."

printf "\nSpringBoot 애플리케이션을 실행합니다.\n"

JAR_PATH=$(ls $PROJECT_PATH/$PROJECT_BUILD_PATH/ | grep .jar | head -n 1)
sudo -E nohup java -jar -Dspring.profiles.active=prod $PROJECT_PATH/$PROJECT_BUILD_PATH/$JAR_PATH &
