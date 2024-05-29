### 예약

- [x] 예약 생성시 paymentKey, orderId, amount를 입력받는다.

### 결제 요청

- [x] toss의 결제 승인 API를 호출한다.
    - https://api.tosspayments.com/v1/payments/confirm
- [x] 결제 승인이 실패하면 에러를 핸들링한다.
- [x] RestClient의 timeout을 설정한다.

결제 승인 예외 케이스

1. 결제 요청 후 10분 이내에 결제 승인이 되지 않았을 경우 -> NOT_FOUND_PAYMENT_SESSION (404) -> 클라이언트가 승인을 제 시간에 안해서 -> 404
2. 카드사가 해당 카드를 거절했을 경우 -> REJECT_CARD_COMPANY (403) -> 클라이언트의 잘못된 카드 -> 403
3. API 키값(secretKey) 또는 주문번호(orderId)가 최초 요청한 값과 다를 경우 -> FORBIDDEN_REQUEST (403) -> 서버가 잘못 보내서 -> 500
4. API 키를 잘못 입력했을 경우 -> UNAUTHORIZED_KEY (401) -> 서버가 잘못 보냄 -> 500

api secret key : test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6
