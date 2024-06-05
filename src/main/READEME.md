#  방탈출 결제 / 배포

## 기능 구현 사항
- [x] 예약
  - [x] 날짜, 시간, 테마를 선택하여 방탈출 예약을 할 수 있다
  - [x] 예외 
   - [x] 결제 성공 + 예약 저장 실패 시 >  결제를 취소한다
   - [x] 결제 실패 시 > 예약을 저장하지 않고 예외를 발생시킨다 


- [x] 외부 결제 Api를 연동한다
  - [x] 예약 실패 시, 결제 실패 사유를 알 수 있어야 한다.

- [x] 내 예약 조회
  - [x] 결제 정보를 함께 볼 수 있다
    - [x] paymentKey, 결제 금액을 볼 수 있다

### Api 연동 세부사항
- api 세부 정보
  - yml 파일 `payment`에서 관리한다
  - secret-key: 결제 승인 비밀 키
  - approve-url : 결제 승인 url
  - cancel-url : 결제 취소 url
- timeout
  - 최대 1번의 패킷 유실 시간을 고려한다. 
  - connection timeout : 3초
  - read timeout : 1초
- 예외 처리
  - 최대 재시도 횟수 : 5회
  - Enum 클래스인 exception.ApiBadRequestExceptions : 토스 페이먼트 측 메시지 + 400 반환
  - 그 외 예외 : `결제 과정에서 문제가 발생했습니다.` + 500반환

