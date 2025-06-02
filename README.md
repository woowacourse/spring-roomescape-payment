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
