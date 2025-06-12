echo "1. Git 확인하고  pull"
if [ -d "spring-roomescape-payment" ]; then
  echo "기존 폴더 있음. git pull 실행"
  cd spring-roomescape-payment || exit 1
  git checkout step2 || {
    echo "step2 브랜치 없음"
    exit 1
  }
  git pull origin step2 || exit 1
else
  echo "폴더 없으니 git clone 실행합니다."
  git clone https://github.com/Byesol/spring-roomescape-payment.git
  cd spring-roomescape-payment || exit 1
  git checkout step2 || {
    echo "step2 브랜치가 없습니다."
    exit 1
  }
fi

echo "2. 기존 8080 프로세스 종료"
PID=$(lsof -ti :8080)
if [ -n "$PID" ]; then
  echo "8080 포트를 사용하는 프로세스 종료 시도 (PID: $PID)"
  kill -9 "$PID" && echo "성공적으로 종료됨" || echo "종료 실패"
else
  echo "열려있는 8080 포트가 존재하지 않음"
fi

echo "3. Gradle bootJar"
./gradlew bootJar || exit 1

echo "4. 앱 실행 준비"
cd build/libs || exit 1
JAR_FILE=$(ls spring-roomescape-payment*.jar 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "JAR 파일을 찾지 못했습니다."
  exit 1
fi

rm -f nohup.out

echo "5. 서버를 백그라운드에서 실행합니다."
nohup java -jar "$JAR_FILE" > app.log 2>&1 &

echo "완료. tail -f spring-roomescape-payment/build/libs/app.log 를 통해 로그 확인가능합니다."