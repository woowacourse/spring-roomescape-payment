# 기능 요구사항 목록
- [x] 사용자가 날짜, 테마, 시간을 선택하고 결제를 해야 예약할 수 있도록 변경한다.
- [x] 결제 기능은 외부의 결제 서비스를 사용하여 외부의 결제 API를 연동한다.
- [x] 결제 승인 API 호출에 실패 한 경우, 안전하게 에러를 핸들링 한다.
- [x] 사용자는 예약 실패 시, 결제 실패 사유를 알 수 있어야 한다.
- [X] 내 예약 페이지에서 예약 정보 외에 결제 정보도 함께 볼 수 있도록 수정한다.
  - [X] 내 예약 페이지에서 필수로 확인 할 수 있어야 하는 결제 정보는 paymentKey, 결제 금액이다.
  - [X] 그 외 결제 정보는 DB에 선택적으로 저장한다.
- [X] 예약 도메인에 결제를 필드로 가지도록 수정
  - [ ] 예약 신청 시 예약자가 없는 경우 결제까지 일어난다.
  - [X] 예약 대기인 경우 결제 없이 예약 대기가 된다.
  - [ ] 앞선 예약이 삭제된 경우, 바로 다음 사용자는 결제 대기 상태로 들어간다.
  - [ ] 결제 대기 사용자는 결제 버튼을 눌러 결제를 진행해야 결제 완료가 된다.