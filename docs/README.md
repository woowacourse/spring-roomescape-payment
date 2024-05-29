## 기능 구현 목록

클라이언트-서버 결제 승인 요청  
- [x] 사용자 예약 요청 시 기존 예약 정보에 결제 정보를 추가하여 보낸다.
  - EndPoint : `POST /reservations`  
- [x] request body에 필요한 데이터는 다음과 같다.  
    `date`: 예약 일자,  
    `themeId`: 테마 id,  
    `timeId`: 예약 시간 id,  
    `paymentKey`: 페이먼트 키,  
    `orderId`: 주문 id,  
    `amount`: 결제 금액
- [x] 결제 승인 시 201 코드를 응답한다. 
- [ ] 결제 실패 시 400 또는 500 코드를 응답한다.

서버-토스 결제 승인 요청
- [x] 클라이언트 측에서 받은 데이터로 토스에 결제 승인을 요청한다.
  - EndPoint : `POST https://api.tosspayments.com/v1/payments/confirm` 
- [x] 요청 시 다음과 같이 Basic 인증 헤더를 설정한다.
  - `Authorization: Basic base64("{WIDGET_SECRET_KEY}:")`
- [x] request body에 필요한 데이터는 다음과 같다.  
    `orderId` : 주문 id  
    `amount` : 결제 금액  
    `paymentKey` : 페이먼트 키  
- [x] 결제가 승인된 경우, Payment 객체가 응답된다.
- [ ] 결제에 실패한 경우, 에러 코드가 응답된다.
