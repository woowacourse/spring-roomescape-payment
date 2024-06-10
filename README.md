# [ERD](https://www.erdcloud.com/d/i7o7W64kLhNj9TW5m)

<img width="1218" src="https://github.com/ChooSeoyeon/spring-roomescape-payment/assets/83302344/d46b96ef-b589-433c-90dd-5294d3f499f6">

# 기능 명세

## 1단계 - 에약 시 결제 단계 추가

- [x] 결제창 UI 추가
    - [x] 결제창에서 결제수단을 선택하고 토스페이먼츠에게 결제 요청을 함 [2]
    - [x] 서버에게 결제정보를 전달함 [3]
- [x] 토스페이먼츠에게 결제승인 API를 호출함 [4]
    - 사용자 예약 추가 API Request에 결제승인을 위한 정보(paymentKey, orderId) 추가
- [x] 결제승인 API 호출에 실패 한 경우, 에러 핸들링 [4]

## 2단계 - 내 예약 페이지 변경

- [x] 결제 테이블 추가
    - 결제 정보: paymentKey, type, orderId, orderName, currency, method, totalAmount, status
- [x] 사용자 예약 추가 시 결제 정보를 DB에 저장
- [x] 내 예약 조회 API Response에 결제 정보 추가
    - 결제 정보: paymentKey, currency, totalAmount, status

## 3단계 - 배포하기

- [x] WAS 서버 배포

## 4단계 - 문서화

- [x] ERD 작성
- [x] RestDocs 적용

## 5단계 - 리팩토링

- [ ] Logger 추가
- [ ] 대기 테이블과 예약 테이블 간 연관관계 제거
    - 예약결제(결제정보, 예약, 회원)
    - 예약(예약정보, 회원)
    - 대기(예약정보, 회원)
- [ ] 예약, 결제 플로우에 soft delete 적용(삭제 -> 취소)
    - 예약상태에 '예약취소' 값 추가
    - 하나의 예약에 여러 회원의 결제 정보 존재 가능
- [ ] LazyLoading과 OSIV 적용 여부 고민
- [ ] 테마 별 금액 적용

### 결제 플로우

- [1] 클라이언트가 토스페이먼츠에게 결제위젯 렌더 요청
- [2] 클라이언트가 토스페이먼츠에게 결제수단 선택 및 결제 요청
- [3] 클라이언트가 서버에게 결제정보를 전달
- [4] 서버가 토스페이먼츠에게 결제승인 API[외부 API]  호출-> API 호출 실패 시, 에러 핸들링
- [5] 토스페이먼츠가 클라이언트에게 결제승인 결과 안내

# 팀 컨벤션

- DTO
    - 직렬화/역직렬화 대상이 되는 DTO는 class로 구현한다
        - 네이밍 컨벤션: xxxRequest, xxxResponse
        - 목적: 직렬화/역직렬화에 필요한 코드만 제공하며 동작 원리 학습
    - 직렬화/역직렬화 대상이 되지 않는 DTO는 record로 구현한다
        - 네이밍 컨벤션: xxxInput, xxxOutput
