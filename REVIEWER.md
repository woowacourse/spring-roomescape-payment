
#### 배포 스트립크는 누가 사용하는지 따로 요구사항이 주어지지 않아 제 local에서만 배포할 수 있도록 설정해놨습니다.
### deploy.sh
```
#!/bin/bash
EC2_HOST="ubuntu@ec2-3-36-57-197.ap-northeast-2.compute.amazonaws.com"
PEM_KEY_PATH="/Users/spqje/key-jenson.pem"
TARGET_DIR="~/home/ubuntu/app"
JAR_NAME="spring-roomescape-payment-0.0.1-SNAPSHOT.jar"
TARGET_JAR_PATH="$TARGET_DIR/$JAR_NAME"

./gradlew clean build -x test

scp -i "$PEM_KEY_PATH" "build/libs/$JAR_NAME" "$EC2_HOST:$TARGET_JAR_PATH"

ssh -i "$PEM_KEY_PATH" "$EC2_HOST" << EOF
  cd ~/app
  pkill -f "$JAR_NAME" || true
  nohup java -jar "$JAR_NAME"
EOF
```


#### yml 파일은 main, test 의 내용이 다릅니다.
#### application.yml은 src/main/resources 경로에,
#### application.test.yml은 src/test/resources 경로에 설정 부탁드립니다.


### application.yml
```
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:database
    username: sa
    password:

  sql:
    init:
      mode: always
      data-locations: classpath:data.sql

  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
    defer-datasource-initialization: true

security:
  jwt:
    token:
      secret-key: s8R$kQz3nZ!L5pT7c@J#xM9vB1&uD6hE
      access:
        expire-length: 600000 # 10 분

scheduler:
  request:
    time: 60000 
  valid:
    period: 60s


logging:
  level:
    root: info
```

### application.test.yml
```
spring:
  h2:
    console:
      enabled: true
      path: /h2-console

  sql:
    init:
      mode: always
      data-locations: classpath:test.sql

  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
    defer-datasource-initialization: true

security:
  jwt:
    token:
      secret-key: s8R$kQz3nZ!L5pT7c@J#xM9vB1&uD6hE
      access:
        expire-length: 600000 # 10 분

scheduler:
  request:
    time: 600 
  valid:
    period: 1s

```