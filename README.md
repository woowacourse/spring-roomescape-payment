결제에 관련한 테이블이 필요
-> "결제" 도메인이 필요함
예약과 결제는 1:1 관계

### 예약
- [ ] 예약 생성시 paymentKey, orderId, amount를 입력받는다.
- 

### 결제 요청
- [ ] toss의 결제 승인 API를 호출한다.
  - https://api.tosspayments.com/v1/payments/confirm
- [ ] 결제 승인이 성공하면 결제 정보를 저장한다.
- [ ] 결제 승인이 실패하면 에러를 핸들링

결제 승인 예외 케이스
1. 결제 요청 후 10분 이내에 결제 승인이 되지 않았을 경우 -> NOT_FOUND_PAYMENT_SESSION
2. 카드사가 해당 카드를 거절했을 경우 -> REJECT_CARD_COMPANY
3. API 키값(secretKey) 또는 주문번호(orderId)가 최초 요청한 값과 다를 경우 -> FORBIDDEN_REQUEST
4. API 키를 잘못 입력했을 경우 -> UNAUTHORIZED_KEY

api secret key : test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6

