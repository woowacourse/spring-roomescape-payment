### 예약

- [x] 예약 생성시 paymentKey, orderId, amount를 입력받는다.
-

### 결제 요청

- [x] toss의 결제 승인 API를 호출한다.
    - https://api.tosspayments.com/v1/payments/confirm
- [ ] 결제 승인이 성공하면 결제 정보를 저장한다.
- [x] 결제 승인이 실패하면 에러를 핸들링한다.

결제 승인 예외 케이스

1. 결제 요청 후 10분 이내에 결제 승인이 되지 않았을 경우 -> NOT_FOUND_PAYMENT_SESSION (404) -> 클라이언트가 승인을 제 시간에 안해서 -> 404
2. 카드사가 해당 카드를 거절했을 경우 -> REJECT_CARD_COMPANY (403) -> 클라이언트의 잘못된 카드 -> 403
3. API 키값(secretKey) 또는 주문번호(orderId)가 최초 요청한 값과 다를 경우 -> FORBIDDEN_REQUEST (403) -> 서버가 잘못 보내서 -> 500
4. API 키를 잘못 입력했을 경우 -> UNAUTHORIZED_KEY (401) -> 서버가 잘못 보냄 -> 500

api secret key : test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6

우리의 고민
토스에게 결제 조회 api를 요청해야 하는가?

- 어차피 어떤 예약의 결제 정보인지 알려면 우리 db에도 paymentKey를 저장해야 한다
- 만약 예약이 100개면 나의 예약 조회할 때마다 토스에게 100개의 GET 요청을 보내야 한다
  -> 결론: LMS는 연동 테스트를 위해서 한 번 해봐~이다. 고로 무시한다.

예약에 필드를 추가할 것인가 vs 결제 관련 테이블을 추가하고 예악과 연관 관계를 설정할 것인가 -> 결제 성공시

- id, paymentKey, amount, reservation_id
