## 요구사항 분석

### 예약
- [x] 예약 정보는 식별자, 이름, 일정, 예약 상태로 이뤄져있다.
    - [x] 이름은 1자 이상, 20자 이하여야 한다.
- [x] 사용자의 식별자를 통해 해당하는 예약을 조회할 수 있다.
- [x] 중복으로 예약/예약 대기를 할 수 없다.
  - [x] 예약이 이미 존재하는데 예약을 추가하려고 하면 자동으로 예약 대기로 생성된다.(어드민도 동일)
  - [x] 이미 예약 상태인데 예약/예약 대기를 요청할 수 없다.
- [x] 사용자는 예약을 요청할 수 있다.
  - [x] 사용자가 요청한 일정과 테마로 예약이 존재하면, 예약 대기 상태로 생성된다.
- [x] 어드민은 예약을 삭제할 수 있다.
  - [x] 사용자는 예약을 삭제할 수 없다.
- [x] 사용자는 예약 대기를 취소할 수 있다.
  - [x] 이미 승인한 예약 대기를 취소할 수 없다.
  - [x] 사용자는 본인의 예약 대기만 취소할 수 있다.
  - [x] 사용자는 몇번째 예약 대기인지 알 수 있다.
  - [x] 이미 예약으로 전환된 예약 대기 취소를 할 수 없다.
  - [x] 사용자는 예약을 삭제할 수 없다.
- [x] 사용자는 예약 대기를 요청할 수 있다.
  - [x] 예약이 존재하지 않으면, 예약 상태로 생성된다.
  - [x] 이미 예약 대기 상태인데 예약/예약 대기를 요청할 수 없다.
- [x] 어드민은 모든 예약 대기 목록을 조회할 수 있다.
- [x] 어드민은 모든 예약 목록을 조회할 수 있다.
- [x] 어드민은 예약 대기와 예약을 취소할 수 있다.
  - [x] 일정이 지난 예약은 삭제할 수 없다.

### 예약 대기 - 자동
- [x] 전재: 예약이 있어야 예약 대기가 존재할 수 있다.
- [x] 예약이 없을 때 예약 대기 요청이 들어올 수 없다.
- [x] 예약이 있을 때 예약 요청이 들어올 수 없다.
- [x] 어드민이 예약을 삭제하면 자동으로 예약 대기 1순위가 예약으로 변경된다.

### 예약 정보
- [x] 예약 정보는 테마, 날짜, 시간으로 이루어져있다.

### 일정
- [x] 일정은 날짜, 예약 시간으로 이뤄져있다.
    - [x] 일정은 현재 이후여야 한다.
    - [x] 날짜는 올바른 형식으로 주어져야 한다.
    - [x] 예약 시간은 이미 존재하는 시간들 중 하나이어야 한다.
    - [x] 테마는 이미 존재하는 테마들 중 하나이어야 한다.

### 시간
- [x] 시간 정보는 식별자, 시작하는 시간으로 이뤄져있다.
- [x] 시간은 삭제할 수 있다
  - [x] 예약이 존재하는 시간은 삭제할 수 없다.
- [x] 시간의 추가, 삭제는 관리자만 할 수 있다.

### 테마
- [x] 테마는 식별자, 이름, 설명, 썸네일로 이뤄져있다.
    - [x] 이름은 중복될 수 없다.
    - [x] 이름은 1자 이상, 20자 이하여야 한다.
    - [x] 설명은 100자를 초과할 수 없다.
- [x] 테마는 삭제할 수 있다.
  - [x] 예약이 존재하는 테마는 삭제할 수 없다.
- [x] 테마의 추가, 삭제는 관리자만 할 수 있다.

### 사용자
- [x] 사용자는 식별자, 이름, 이메일, 비밀번호, 역할로 이뤄져있다.
    - [x] 이름은 1자 이상, 20자 이하여야 한다.
    - [x] 이메일은 중복될 수 없다.
    - [x] 비밀번호는 6~12자리 이내이어야 한다.
- [x] 모든 사용자의 정보는 어드민만 볼 수 있다.

### 결제
- [x] 사용자가 날짜, 테마, 시간을 선택하고 결제를 해야 예약할 수 있도록 변경.
  - [ ] 예약이 취소될 경우
    - [ ] 예약이 존재하지 않으면서 1번째 예약 대기일때 결제 허용
    - [ ] 사용자가 결제 작업 진행 -> 결제 완료 후에 예약 상태로 변경
- [ ] 사용자가 예약을 취소할때, 결제도 함께 취소된다.
  - [ ] 결제가 취소되어야만 예약이 취소된다.
- [x] 결제 기능은 외부의 결제 서비스를 사용하여 외부의 결제 API를 연동한다.
- [x] 결제 승인 API 호출에 실패 한 경우 안전하게 예외가 발생한다.
  - [x] 결제 승인, 결제 취소 에러 참고