echo "=== 배포 시작 ==="

APP_DIR="spring-roomescape-payment"
REPO_URL="https://github.com/Ryan-Dia/spring-roomescape-payment.git"
BRANCH="step2"

# 기존 디렉토리 삭제 후 클론
if [ -d "$APP_DIR" ]; then
  echo "[INFO] 기존 디렉토리 삭제"
  rm -rf "$APP_DIR"
fi

echo "[INFO] Git 클론 중..."
git clone -b "$BRANCH" "$REPO_URL"

echo "[INFO] 빌드 시작"
./gradlew clean build

echo "[INFO] 기존 애플리케이션 종료"
pkill -f 'spring-roomescape-payment'

echo "[INFO] 앱 실행"
nohup java -jar build/libs/spring-roomescape-payment-0.0.1-SNAPSHOT.jar > ../ap>
cd ..

echo "[INFO] 배포 완료"
