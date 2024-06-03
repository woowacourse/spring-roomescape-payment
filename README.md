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
