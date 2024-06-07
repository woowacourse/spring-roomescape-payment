## 로그인 계정
- 로그인 가능한 계정
    - 관리자 계정 - id : admin@abc.com / pw : 1234 / name : 관리자
    - 일반 유저 계정 - id : bri@abc.com / pw : 1234 / name : 브리
    - 일반 유저 계정 - id : brown@abc.com / pw : 1234 / name : 브라운
    - 일반 유저 계정 - id : duck@abc.com / pw : 1234 / name : 오리

## 예약 및 대기 정책
- 예약 취소
  - 예약 대기자가 있는 경우 자동으로 예약 승인 됨
- 예약 대기 요청
  - 해당 예약이 존재해야 한다.
  - 해당 예약이 현재 날짜 이후이어야 한다.
  - 같은 예약 및 예약 대기에 해당 유저가 없어야 한다. (중복 예약 불가)

## 1단계 요구사항
- 사용자가 날짜, 테마, 시간을 선택하고 결제를 해야 예약할 수 있도록 변경한다.
  - 결제 승인 API 호출에 실패 한 경우, 안전하게 에러를 핸들링 한다.
  - 사용자는 예약 실패 시, 결제 실패 사유를 알 수 있어야 한다.

## 2단계 요구사항

#### 목표
- 내 예약 페이지에서 예약 정보 외에 결제 정보도 함께 볼 수 있도록 수정합니다.
  - paymentKey, 결제 금액은 내 예약 페이지에서 필수로 확인 할 수 있어야 한다.
  - 그 외 결제 정보는 DB에 선택적으로 저장한다.

#### 2단계 요구사항에 따른 정책

- 예약 취소
  - 예약 취소 시, 자동으로 환불됨
  - 예약 대기자는 '결제 대기' 상태로 자동 승인 됨
- 유저 예약
  - 예약 & 결제로 변경 (Endpoint 2개)
- 어드민 예약
  - 따로 결제 없이 예약됨

#### 리팩토링
- [x] `Reservation` Table의 일부 컬럼(`date`, `timeId`, `themeId`)을 `Schedule` Table로 분리
  - [ ] `Reservation`의 CRUD 정상 작동 확인
  - [ ] `ReservationRepository`의 snake_case 지우기
- [ ] `Waiting`이 `reservationId` 대신 `scheduleId`를 가지고 있도록 변경
  - [ ] `Waiting`의 CRUD 정상 작동 확인

#### 결제 정보 저장 및 조회 도입
- [ ] `Payment` Table 도입
  - scheduleId, memberId, status(PAID, REFUND)로 구성
- [ ] `Reservation` Table에 status 도입
  - 'ADMIN_RESERVE', 'WAITING_FOR_PAYMENT', 'DONE_PAYMENT' 상태 도입
- [ ] 마이 페이지 조회 기능 변경
  - paymentKey, 결제 금액이 보이도록 변경

#### 결제 관련 기능 추가
- [ ] 마이 페이지 기능 추가
  - [ ] 결제 취소 시, 결제 금액이 환불되도록 추가
  - [ ] 결제 대기 상태에서 결제할 수 있도록 추가
- [ ] 어드민 예약 기능 변경
  - [ ] 어드민이 예약을 할 경우, '어드민 결제' 상태로 예약되도록 변경
  - [ ] 어드민이 예약을 취소할 경우, 결제 금액이 환불되도록 추가
