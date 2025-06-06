# 예약 결제

## 1단계 - 예약 시 결제단계 추가

- [x] 클라이언트 코드를 수정한다. [1단계 클라이언트 커밋](https://github.com/woowacourse/spring-roomescape-member/commit/b7348d5bde416124e9a9e849a7e52cfb2dc2b1b8)
- [x] 사용자는 날짜, 테마, 시간을 선택하고 결제한다.
- [x] 결제 기능은 외부 결제 서비스를 이용한다.
- [x] 결제 승인 API 호출에 실패 한 경우 예외를 응답을 제공한다.

## api 

예약 request
```json

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

## 2단계 - 내 예약 페이지 변경
- [x] 내 예약 페이지에서 예약 정보 외에 결제 정보도 함께 볼 수 있도록 수정한다.
- [x] 필수로 확인할 수 있어야 하는 결제 정보는 `paymentKey`, 결제 금액이다.
- [x] 클라이언트 코드를 수정한다. [2단계 클라이언트 코드 커밋](https://github.com/woowacourse/spring-roomescape-member/commit/38b69424e37d267e555f2efed2de148d94b637ce)

## 3단계 - 배포하기
- [x] 셸 스크립트를 작성하여 코드를 서버에 배포한다.

## 4단계 - 문서화, 로깅
- [ ] 클라이언트 개발자가 이해하기 좋은 형태로 `사용자 예약 페이지`의 API 문서를 작성한다.
- [ ] Database ERD를 작성한다.
- [ ] log 라이브러리를 선택해 모니터링 및 에러 트래킹을 위해 로그 레벨을 구분하여 로그를 기록한다.
