# EC2 접속
ssh -i "./key-dompoo.pem" ubuntu@ec2-3-35-14-94.ap-northeast-2.compute.amazonaws.com;
echo "서버 접속 완료";

# 최신 커밋 불러오기
cd ~/spring-roomescape-payment;
git checkout main;
git pull origin main;
echo "최신 커밋 불러오기 완료";

# 빌드
./gradlew clean bootJar;
echo "빌드 완료";

# 스프링 애플리케이션 실행
cd build/libs;
lsof -t -i:8080 | xargs kill;
nohup java -jar spring-roomescape-payment-0.0.1-SNAPSHOT.jar &
echo "애플리케이션 시작 완료";
