# 방탈출 결제 / 배포

## 1단계 요구사항
- [x] 사용자 예약 페이지 변경
  - [x] 결제창 UI 추가
- [ ] 사용자가 날짜, 테마, 시간을 선택하고 결제까지 성공 해야 예약할 수 있도록 변경
  - [ ] 결제 성공하면(200 OK) -> 예약 진행

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

- [x] 방탈출 금액 테마 테이블에 저장(price)
  - [x] 테마 저장 요청 시 price도 받아옴
- [x] 테마 선택 시 price 서버에서 받아 오기

- [ ] 토스 결제 API 연동
  - [ ] 결제 승인 API 호출에 실패 한 경우, 안전하게 에러를 핸들링
    - 에러 객체
    ```json
    {
    "code": "NOT_FOUND_PAYMENT",
    "message": "존재하지 않는 결제 입니다."
    }
    ```
    - 결제 승인 API 테스트시, 에러응답을 얻을 수 있는 시도
      - 시크릿 키를 정상적이지 않은 값으로 요청
      - paymentkey를 클라이언트에서 획득하지 않은 값으로 요청
      - 이미 성공한 paymentKey로 요청
      - INVALID_API_KEY, UNAUTHORIZED_KEY, INCORRECT_BASIC_AUTH_FORMAT
- [ ] 예약 실패 시, 결제 실패 사유 응답
