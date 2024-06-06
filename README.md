# 방탈출 기능 요구 사항

- [API 요청 폴더 바로가기](api-test)

### 📌 reservation (예약)
- [예약 API 요청 파일 바로가기](api-test/reservation-api.http)

##### 예약 기능 목록
- [x] 예약을 추가할 수 있다.
  - [x] 결제를 완료해야 예약이 추가된다.
  - [x] 예약자 명은 4글자까지 입력 가능하다.
  - [x] 예약자 명은 한글로만 입력 가능하다.
  - [x] 예약자 명을 null, 빈 문자열, 공백으로 요청할 수 없다.
- [x] 예약을 id로 조회할 수 있다.
- [x] 전체 예약을 조회할 수 있다.
- [x] 예약을 id로 제거할 수 있다.
- [x] 지나간 날짜로 예약할 수 없다.
- [x] 중복 예약은 불가능하다.
  - ex:) 이미 4월 1일 10시에 예약이 되어있다면, 4월 1일 10시에 대한 예약을 생성할 수 없다.
- [x] 테마 아이디, 회원 아이디, 기간 조건 별로 조회할 수 있다.
- [x] 회원별 예약 목록을 조회할 수 있다.
  - [x] 예약 대기를 걸어 놨을 경우 대기 순서를 볼 수 있다.
  - [x] 결제 정보(paymentKey, 결제 금액)를 함께 볼 수 있다.
- [x] 이미 예약이 되어 있다면 예약 대기를 걸 수 있다.
  - [x] 본인이 한 예약과 동일한 예약에 대기를 추가로 걸 수 없다.
- [x] 확정된 예약을 예약자가 취소할 수 있다.
  - [x] 예약 당일 1일 전 (자정)까지만 가능하고 예약 당일에는 취소가 불가능하다.
  - [x] 예약자의 예약이 취소될 경우 1번째 예약 대기자가 자동으로 예약 확정된다.
  - [x] 예약이 취소될 때 결제 내역이 있으면 함께 취소된다.

---
### 📌 payment (결제)
- [결제 API 요청 파일 바로가기](api-test/payment-api.http)

##### 결제 기능 목록
  - [x] 결제 기능은 Toss Payment API를 사용한다.
  - [x] 결제 승인에 실패한 경우 예외를 발생 시킨다.
    - [x] 응답으로 결제 실패 사유를 반환한다.

---
### 📌 theme (테마)
- [테마 API 요청 파일 바로가기](api-test/theme-api.http) <br>

##### 테마 기능 목록
- [x] 테마를 추가할 수 있다.
  - [x] 테마 명을 null, 빈 문자열, 공백으로 요청할 수 없다.
  - [x] 테마 명은 10글자까지 입력 가능하다.
  - [x] 테마 명에 공백을 포함할 수 있고  한글, 영어, 숫자만 입력가능하다.
  - [x] 설명을 null, 빈 문자열, 공백으로 요청할 수 없다.
  - [x] 설명에는 쉼표, 구두점, 공백, 한글, 숫자만 입력가능하다.
  - [x] 설명은 50자까지 입력 가능하다.
  - [x] 썸네일 주소를 null, 빈 문자열, 공백으로 요청할 수 없다.
- [x] 테마를 id로 조회할 수 있다.
- [x] 전체 테마을 조회할 수 있다.
- [x] 최근 1주일 기준, 인기 테마 10개를 조회할 수 있다.
- [x] 테마를 id로 제거할 수 있다.
- [x] 중복된 테마명은 추가할 수 없다.

---
### 📌 reservationTime (예약시간)
- [예약 시간 API 요청 파일 바로가기](api-test/reservationtime-api.http) <br>

##### 예약 시간 기능 목록
- [x] 예약 시간을 추가할 수 있다.
  - [x] 시작 시간을 null로 요청할 수 없다.
- [x] 예약 시간을 id로 조회할 수 있다.
- [x] 지정한 날짜와 테마의 예약 가능한 시간을 조회할 수 있다.
- [x] 전체 예약 시간을 조회할 수 있다.
- [x] 예약 시간을 id로 제거할 수 있다.
- [x] 특정 시간에 대한 예약이 존재하는데, 그 시간을 삭제 할 수 없다.

---
### 📌 member (회원)
- [사용자 API 요청 파일 바로가기](api-test/member-api.http) <br>

##### 회원 기능 목록
- 회원 가입
  - [x] 회원가입으로 사용자 정보를 추가할 수 있다.
    - [x] 회원 이름은 4글자까지 입력 가능하다.
    - [x] 회원 이름을 null, 빈 문자열, 공백으로 요청할 수 없다.
    - [x] 이메일은 이메일 형식으로 입력 가능하다.
  - [x] 이미 가입된 이메일이라면 예외가 발생한다.

- 로그인
  - [x] 가입된 회원일 경우 로그인에 성공하고 메인 페이지로 이동 시킨다.
    - [x] 존재하지 않는 회원 정보일 경우 예외가 발생한다.
  - [x] 인증된 사용자가 아닐 때 다른 페이지 접근 시 로그인 페이지로 이동 시킨다.

- 로그아웃
  - [x] 로그아웃 시 쿠키를 만료 시킨다.

- 예약 관리 (관리자 권한 회원)
  - [x] 예약 대기 목록을 조회할 수 있다.
    - [x] 현재 날짜 이후의 예약 대기 목록만 조회한다.
  - [x] 대기 상태의 예약을 거절할 수 있다.
