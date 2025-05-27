# 예약 결제

## 1단계 - 예약 시 결제단계 추가

- [x] 클라이언트 코드를 수정한다. [1단계 클라이언트 커밋](https://github.com/woowacourse/spring-roomescape-member/commit/b7348d5bde416124e9a9e849a7e52cfb2dc2b1b8)
- [ ] 사용자는 날짜, 테마, 시간을 선택하고 결제한다.
- [ ] 결제 기능은 외부 결제 서비스를 이용한다.
- [ ] 결제 승인 API 호출에 실패 한 경우 예외를 응답을 제공한다.

## api 

예약 request
```json

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
