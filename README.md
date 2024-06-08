# 요구사항 분석 정리

## 1단계 - 예약 시 결제단계 추가

- [x] 예약 시 결제 기능 구현
    - [x] 외부의 결제 API 연동
    - [x] 클라이언트가 토스 API 를 통해 결제 요청
    - [x] 토스 API 가 클라이언트에게 PaymentKey 를 반환
    - [x] 서버가 클라이언트의 amount, orderId, PaymentKey 를 가져와 DB 와 비교
    - [x] 서버는 토스 API 로부터 받은 amount 를 DB 와 비교
        - [x] 승인 요청 이후 토스 API 의 응답에 따라 성공, 실패 응답
        - [x] 다르다면 서버는 클라이언트로 예외 응답
    - [x] 결제 승인 API 호출에 실패 시 예외 처리

## 2단계 - 내 에약 페이지 변경

- [x] 예약 성공 후 내 예약 페이지에 `paymentKey`와 `amount` 컬럼 추가
    - [x] reservation 엔티티에 `Payment` 객체 추가
    - [x] `paymentService.confirm()`이 Payment를 반환하도록 변경
        - [x] log에서 사용하는 response.getBody()의 InputStream 복사하는 Wrapper 객체 추가
    - [x] reservation-mine.js 변경

## 3단게 - 배포하기

- [x] 서버에 배포하기
    - [x] 프로덕션 h2 데이터베이스를 mysql로 변경
    - [x] 테스트, 프로덕션 `application.properties` 분리
    - [x] doocker-comopose.yml 작성

## 4단게 - 문서화

- [x] 테스트에 restDocs 적용
    - [x] index.adoc 파일 작성
