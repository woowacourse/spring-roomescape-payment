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

## 정책

* 예약시 결제
* 예약대기시 결제

## 리팩터링 목록

* [ ] 예약 대기 취소시 환불 기능 구현
* [ ] 커스텀 예외 구현 방식 변경
* [ ] 결제 오류 처리 로직 변경
* [ ] 유저, 어드민 예약 저장 코드 중복 문제 해결
