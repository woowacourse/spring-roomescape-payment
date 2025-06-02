# 방탈출 결제 / 배포 🚪

## 📌 소개

- 방탈출 예약을 관리할 수 있는 웹 애플리케이션입니다.
- 토스페이먼츠를 통한 결제 기능을 제공합니다.
- 예약 시간 관리 기능을 제공합니다.
- 사용자 친화적인 UI/UX를 제공합니다.

## 📌 기능

### 📋 Reservation(예약)

- 예약 전체 조회
- 예약 추가
- 예약 삭제
- 예약 대기 기능
- 내 예약 목록 조회

### 📋 Reservation Time(예약 시간)

- 예약 시간 전체 조회
- 예약 가능한 시간 조회
- 예약 시간 추가
- 예약 시간 삭제

### 📋 Theme(테마)

- 테마 전체 조회
- 인기 테마 조회
- 테마 추가
- 테마 삭제

### 📋 Payment(결제)

- 토스페이먼츠 결제 연동
- 결제 승인 처리
- 결제 실패 처리

## 📌 API 명세

### 예약 관련 API

| Method | URL                | Description | HTTP Status    |
|--------|--------------------|-------------|----------------|
| GET    | /reservations      | 예약 전체 조회    | 200 OK         |
| POST   | /reservations      | 예약 추가       | 201 CREATED    |
| DELETE | /reservations/{id} | 예약 삭제       | 204 NO CONTENT |
| GET    | /reservations/mine | 내 예약 목록 조회  | 200 OK         |

### 예약 시간 관련 API

| Method | URL              | Description  | HTTP Status    |
|--------|------------------|--------------|----------------|
| GET    | /times           | 예약 시간 전체 조회  | 200 OK         |
| GET    | /times/available | 예약 가능한 시간 조회 | 200 OK         |
| POST   | /times           | 예약 시간 추가     | 201 CREATED    |
| DELETE | /times/{id}      | 예약 시간 삭제     | 204 NO CONTENT |

### 테마 관련 API

| Method | URL          | Description | HTTP Status    |
|--------|--------------|-------------|----------------|
| GET    | /themes      | 테마 전체 조회    | 200 OK         |
| GET    | /themes/rank | 테마 순위 조회    | 200 OK         |
| POST   | /themes      | 테마 추가       | 201 CREATED    |
| DELETE | /themes/{id} | 테마 삭제       | 204 NO CONTENT |

### 인증 관련 API

| Method | URL          | Description | HTTP Status    |
|--------|--------------|-------------|----------------|
| POST   | /login       | 로그인         | 200 OK         |
| POST   | /logout      | 로그아웃        | 204 NO CONTENT |
| GET    | /login/check | 로그인 확인      | 200 OK         |

## 📌 요구사항

### 사용자 예약 기능 개선

- [x] 사용자는 날짜, 테마, 시간을 선택하고 결제 완료 시 예약
- [x] 결제 승인 처리를 위해 외부 결제 API를 연동
    - 외부 API는 테스트용 결제 API를 사용한다.
- [x] 서버에서 결제 승인 API를 직접 호출
- [x] 결제 승인 API 호출이 실패할 경우, 에러를 안전하게 핸들링
    - 실패 시, 사용자에게 결제 실패 사유를 명확히 안내해야 한다.

### 사용자 예약 페이지 UI 변경

- [x] 사용자 예약 페이지에 결제창 UI를 추가

### 사용자 예약 API 요청 변경

- [x] 예약 요청에 결제 승인 정보가 포함되어야 한다.
    - 예약 날짜 (date)
    - 테마 ID (themeId)
    - 시간 ID (timeId)
    - 결제 승인 키 (paymentKey)
    - 주문 번호 (orderId)
    - 결제 금액 (amount)
