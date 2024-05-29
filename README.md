## 기능 요구 사항
### 사용자 예약 생성 및 결제
```
POST /reservations HTTP/1.1
content-type: application/json
cookie: token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6IuyWtOuTnOuvvCIsInJvbGUiOiJBRE1JTiJ9.vcK93ONRQYPFCxT5KleSM6b7cl1FE-neSLKaFyslsZM
host: localhost:8080

{
    "reservationSaveRequest": {
        "date": [
            2030,
            4,
            18
        ],
        "timeId": 1,
        "themeId": 1
    },
    "paymentConfirmRequest": {
        "paymentKey": "key",
        "orderId": "orderId",
        "amount": 1000,
        "paymentType": "none"
    }
}
```
