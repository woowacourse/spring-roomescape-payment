echo "1. Git repo 확인 및 pull"
if [ -d "spring-roomescape-payment" ]; then
  echo "기존 폴더 있음. git pull 실행"
  cd spring-roomescape-payment || exit 1
  git checkout step2 || {
    echo "step2 브랜치 없음"
    exit 1
  }
  git pull origin step2 || exit 1
else
  echo "폴더 없음. git clone 실행"
  git clone https://github.com/horizonpioneer/spring-roomescape-payment.git
  cd spring-roomescape-payment || exit 1
  git checkout step2 || {
    echo "step2 브랜치 없음. 종료"
    exit 1
  }
fi

echo "2. 기존 8080 프로세스 종료"
PID=$(lsof -ti :8080)
if [ -n "$PID" ]; then
  kill -9 "$PID"
  echo "kill (PID: $PID)"
else
  echo "열려있는 8080 포트가 존재하지 않음"
fi

echo "3. Gradle bootJar"
./gradlew bootJar || exit 1

echo "4. 앱 실행 준비"
cd build/libs || exit 1
JAR_FILE=$(ls *.jar | grep 'spring-roomescape-payment' | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "JAR 파일을 찾지 못함"
  exit 1
fi

rm -f nohup.out

echo "5. 서버 실행 (백그라운드)"
nohup java -jar "$JAR_FILE" > nohup.out 2>&1 &

echo "완료. tail -f nohup.out 으로 로그 확인"
