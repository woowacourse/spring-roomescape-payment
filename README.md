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
-

### 비고

- 이번 미션에서는 보안, DB 트랜잭션 관련하여 고려하지 않는다.
