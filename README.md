# 방탈출 예약 애플리케이션

## 배포 주소 및 API 명세서

[배포 주소](http://3.35.231.231:8080/)
[방탈출 API 명세서](https://alstn113.github.io/spring-roomescape-payment/src/main/resources/static/docs/index.html)

## 미션 4 요구사항

- [x] 사용자가 날짜, 테마, 시간을 선택하고 결제를 해야 예약할 수 있다.
- [x] 결제 기능은 외부의 결제 서비스를 사용하여 외부의 결제 API를 연동한다.
- [x] 결제 승인 API 호출에 실패 한 경우, 안전하게 에러를 핸들링 한다.
    - [x] 사용자는 예약 실패 시, 결제 실패 사유를 알 수 있다.

- [x] 결제 테이블을 생성한다.
- [x] 내 예약 페이지를 변경한다.
    - [x] 상태, paymentKey, 결제 금액를 보여준다.
- [x] AWS에 배포한다.
- [x] 문서화를 한다.
    - [x] RestDocs를 사용한다.
