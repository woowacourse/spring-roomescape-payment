# 

## 신규 기능

- [ ] 사용자가 날짜, 테마, 시간을 선택하고 `결제`를 해야 예약 가능하다.
- [ ] 결제 기능은 외부의 결제 서비스를 사용하여 `외부의 결제 API`를 연동한다.
    - 토스 결제 API
- [ ] 결제 승인 API 호출에 실패 한 경우, 에러를 핸들링 한다.
    - [ ] 사용자에게 결제 실패 사유를 제공한다.

## 예외 처리


## API

### 결제 승인 API
- request
```
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
