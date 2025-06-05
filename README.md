## 🚀 1단계 - 예약 시 결제 기능

### 요구사항

- 사용자가 날짜, 테마, 시간을 선택하고 결제를 해야 예약할 수 있어야 한다.
- 결제 기능은 외부의 결제 API를 연동하여 구현한다.

<결제 승인 api Http request 예시>

```http request
POST /reservations HTTP/1.1
content-type: application/json
cookie: token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6ImFkbWluIiwicm9sZSI6IkFETUlOIn0.cwnHsltFeEtOzMHs2Q5-ItawgvBZ140OyWecppNlLoI
host: localhost:8080

{
"date":"2024-03-01",
"themeId":1,
"timeId":1,
"paymentKey":"tgen_20240513184816ZSAZ9",
"orderId":"MC4wNDYzMzA0OTc2MDgy",
"amount":1000
}
```

### 예외 상황

- 시크릿 키를 정상적이지 않은 값으로 요청
- paymentkey를 클라이언트에서 획득하지 않은 값으로 요청
- 이미 성공한 paymentKey로 요청
- 요청 시간 만료

### 비고

- 이번 미션에서는 보안, DB 트랜잭션 관련하여 고려하지 않는다.

## 🚀 2단계 - 내 예약 페이지 변경

### 요구사항

- 내 예약 페이지에서 예약 정보 외에 결제 정보도 함께 볼 수 있도록 수정한다.
- 내 예약 페이지에서 확인해야 하는 결제 정보는 paymentKey, 결제 금액이다.

## 🚀 3단계 - 배포하기

### 요구사항

- 클라우드 서버에 방탈출 예약 서비스를 배포한다.

## 🚀 4단계 - 문서화, 로깅

### 요구사항

- 사용자 예약 페이지에서 호출하는 모든 API를 확인 할 수 있는 API 문서를 작성한다.
- Database ERD 를 작성한다.
- 서비스 운영 시 모니터링 및 에러 트래킹을 위해 로그 레벨(e.g. DEBUG, INFO, WARN, ERROR 등)을 구분하여 로그를 기록한다.

### E-R Diagram

```mermaid
erDiagram
    MEMBER {
        member_id BIGINT PK
        email VARCHAR(255)
        name VARCHAR(255)
        password VARCHAR(255)
        role ENUM "ADMIN, USER"
    }

    PAYMENT {
        paymen_id BIGINT PK
        amount INT
        orderId VARCHAR(255)
        paymentKey VARCHAR(255)
    }

    RESERVATION {
        reservation_id BIGINT PK
        createAt DATETIME
        date DATE
        status ENUM "RESERVED, WAIT, PENDING, CANCEL"
    }

    RESERVATION_TIME {
        time_id BIGINT PK
        startAt TIME
    }

    THEME {
        theme_id BIGINT PK
        description VARCHAR(255)
        name VARCHAR(255)
        thumbnail VARCHAR(255)
    }

    MEMBER ||--o{ RESERVATION: ""
    RESERVATION ||--o| PAYMENT: ""
    RESERVATION }o--|| RESERVATION_TIME: ""
    RESERVATION }o--|| THEME: ""
```
