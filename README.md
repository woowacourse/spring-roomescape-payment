# 방탈출

- [ERD](#erd)
- [Run Guide](#run-guide)
- [API specification](#api-specification)
- [Deployment](#deployment)
- [Tech Skills](#tech-skills)

## ERD

![ERD](https://github.com/woowacourse/spring-roomescape-payment/assets/73146678/20b84911-b884-4268-a2dc-81fc8287176f)

## Run Guide

### Build

```bash
./gradlew bootJar
```

### Test

```bash
./gradlew test
```

### Run

```bash
java -jar build/libs/spring-roomescape-payment-0.0.1-SNAPSHOT.jar
```

## API specification

### Create Document

```bash
./gradlew createDocument
```

> Local: http://localhost:8080/docs/index.html

## Deployment

- Link: [roomescape.chocochip.co.kr](https://roomescape.chocochip.co.kr)
- API 명세서: [roomescape.chocochip.co.kr/docs/index.html](https://roomescape.chocochip.co.kr/docs/index.html)

## Tech Skills

### Spring

- **Java** 17
- **Spring Boot** 3.2.4
- **Spring Data JPA**
- **Validation**

### Docs

- **RestDocs**
- **Asciidoctor**

### DB

- **H2 Database**

### Test

- **JUnit 5**
- **RestAssured**
- **Mockito**

### Web

- **Html**
- **css**
- **JavaScript**

### Infra

- AWS EC2
- nginx
- Let's Encrypt
