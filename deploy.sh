#!/bin/sh
set -e

APP_DIR="spring-roomescape-payment"
APP="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"

echo "깃허브에서 코드 불러오기"
if [ ! -d $APP_DIR ]
then
	git clone https://github.com/jbilee/spring-roomescape-payment.git
fi

cd ~/$APP_DIR

echo "기존 프로세스 종료"
pkill -f $APP

echo "최신 코드로 빌드 및 실행"
git pull origin step2
./gradlew bootJar
cd build/libs
nohup java -jar $APP &
