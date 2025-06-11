## 배포 스크립트 사용법

`scripts/deploy.sh`는 `step2` 브랜치 기준으로 애플리케이션을 빌드 및 실행하는 자동화 스크립트입니다.

## 실행 방법

```bash
chmod +x scripts/deploy.sh  # (최초 1회 실행 권한 부여)
./scripts/deploy.sh
```


### 동작 순서
1. Git 저장소 확인 및 step2 브랜치 pull
   - 현재 디렉토리에 spring-roomescape-payment 폴더가 있으면 해당 폴더로 이동 후 step2 브랜치 체크아웃 및 git pull 실행
   - 폴더가 없다면 GitHub에서 클론 후 step2 브랜치로 이동 
   - step2 브랜치가 존재하지 않으면 스크립트 종료


2. 기존 8080 포트 종료
   - lsof -ti :8080으로 8080 포트를 사용하는 프로세스를 찾고 종료 
   - 포트가 열려있지 않으면 메시지 출력 후 넘어감


3. Gradle 빌드 (bootJar)
   ./gradlew bootJar 명령어를 통해 프로젝트 빌드 
   - 실패 시 스크립트 종료


4. 실행 준비
   - build/libs 디렉토리로 이동 
   - spring-roomescape-payment 이름이 포함된 .jar 파일 탐색 
   - .jar 파일이 없을 경우 스크립트 종료


5. 서버 실행 (백그라운드)
   - 기존 nohup.out 삭제 
   - nohup java -jar 명령어로 애플리케이션 백그라운드 실행 
   - 로그는 nohup.out 파일에 저장됨

