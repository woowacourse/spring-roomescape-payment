set -e

cd /home/ubuntu/spring-roomescape-payment

PID=$(lsof -ti :8080  || true)
if [ -n "$PID" ]; then
  kill -9 "$PID"
fi

git pull

./gradlew bootJar

cd build/libs

nohup java -jar -Dspring.profiles.active=prod spring-roomescape-payment-0.0.1-SNAPSHOT.jar &

echo "쉘 스크립트 실행 완료"