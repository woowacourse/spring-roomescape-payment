## 기능 구현 요구사항

- [x] 사용자 예약 생성
  - [x] 결제 승인 API 호출
  - [x] 결제 실패 시, 사유 반환

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
