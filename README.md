## 1단계 - 예약 시 결제단계 추가

### 예약

- [x] 예약 생성시 paymentKey, orderId, amount를 입력받는다.
- [x] 예약 생성시 결제 승인을 요청한다.

### 결제 승인

- [x] toss의 결제 승인 API를 호출한다.
    - `https://api.tosspayments.com/v1/payments/confirm`
- [x] 결제 승인이 실패하면 에러를 핸들링한다.
    - [x] 사용자는 예약 실패 시, 결제 실패 사유를 알 수 있어야 합니다.
        - FORBIDDEN_REQUEST, UNAUTHORIZED_KEY 에러 코드는 서버에서 발생한 에러이므로 500 status code로 처리한다.
        - 그 외(NOT_FOUND_PAYMENT_SESSION, REJECT_CARD_COMPANY 등)는 4xx status code로 처리한다.
- [x] RestClient의 timeout을 설정한다.

<br>

## 2단계 - 내 예약 페이지 변경

- [ ] 내 예약 페이지에서 예약 정보 외에 결제 정보도 함께 볼 수 있도록 수정한다.
  - 필수로 확인할 수 있어야 하는 결제 정보는 paymentKey, 결제 금액이다.
  - 그 외 결제 정보는 DB에 선택적으로 저장한다.

<br>

## 3단계 - 배포하기

- [x] 클라우드 서버에 방탈출 예약 서비스를 배포한다.

<br>

## 4단계 - 문서화

- [x] `사용자 예약 페이지`에서 호출하는 모든 API를 확인 할 수 있는 API 문서를 작성한다.
- [ ] Database ERD 를 작성하고, 작성 시 테이블 간의 관계를 표현한다.
- [ ] (선택) Logger를 추가하여, 에러 처리의 완성도를 높인다.

