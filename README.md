# 🧩 Escape Room System

방탈출 예약 및 관리 시스템입니다.

---

## 🔐 로그인 시스템

### ✅ 계정 정보

| 역할    | 이메일              | 비밀번호 |
|-------|------------------|------|
| ADMIN | yebink@email.com | 1234 |
| USER  | wooga@gmail.com  | 1234 |

### 📌 로그인 관련 기능

- `GET /login` : 로그인 화면 응답
- `GET /login/check` : 로그인한 멤버의 이름 응답
- `POST /logout` : 토큰 만료 및 로그아웃 처리

---

## 👤 사용자 예약 시스템

### 📌 홈 화면

- `GET /`
    - 홈 화면 응답
    - 최근 일주일 간 가장 예약이 많았던 인기 테마 TOP 10 제공

### 📌 사용자 예약

- `GET /reservation` : 사용자 예약 화면 응답
- `GET /reservations/available-times` : 이용 가능한 시간 목록 응답
- `POST /reservations/waitings` : 예약 대기 등록
- `GET /reservations/mine` : 나의 예약 및 대기 정보 확인
    - 대기 취소 가능
    - **예외 검증** : 지난 날짜에 대한 예약 불가
- 예약 등록 시 **토스페이(TossPay)** 결제 진행

---

## 🛠 관리자 시스템

**모든 `/admin` 요청은 어드민 권한이 필요합니다.**

### 📌 관리자 홈

- `GET /admin` : 어드민 홈 화면 응답

### 📌 예약 관리

- `GET /admin/reservation` : 예약 관리 화면 응답
    - 예약 취소 시 자동으로 대기 예약 승격

#### ▶ 예약 목록 검색

- `GET /admin/searchable-reservations` : 필터 조건에 따른 예약 목록 응답

### 📌 예약 시간 관리

- `GET /admin/time` : 예약 시간 관리 화면 응답

### 📌 테마 관리

- `GET /admin/theme` : 테마 관리 화면 응답

### 📌 예약 대기 관리

- `GET /admin/waitings` : 예약 대기 목록 응답
    - 대기 거절 가능

---

## 📅 예약 API

### ▶ 예약 조회

- `GET /reservations` : 예약 목록 조회

### ▶ 예약 추가

- `POST /reservations` : 예약 등록
    - **예외 검증**
        - 같은 테마, 날짜, 시간의 예약 중복 불가

### ▶ 예약 취소

- `DELETE /reservations/{id}` : 예약 취소

---

## ⏰ 예약 시간 API

### ▶ 예약 시간 추가

- `POST /times`
    - **예외 검증**
        - 예약 시간은 `10:00 ~ 22:00` 사이여야 함
        - 시간 중복 불가
        - 같은 테마, 날짜, 시간에 이미 예약된 경우 등록 불가

### ▶ 예약 시간 조회

- `GET /times` : 예약 시간 목록 조회

### ▶ 예약 시간 제거

- `DELETE /times/{id}`
    - **예외 검증**
        - 해당 시간에 예약이 존재하면 삭제 불가

---

## 🎬 테마 API

### ▶ 테마 추가

- `POST /themas`
    - **예외 검증**
        - 테마 이름 중복 불가

### ▶ 테마 목록 조회

- `GET /themas`

### ▶ 테마 상세 조회

- `GET /themas/{id}`

### ▶ 테마 삭제

- `DELETE /themas/{id}`
    - **예외 검증**
        - 사용 중인 테마(예약 존재)는 삭제 불가

### ▶ 인기 테마 조회

- `GET /themas/popular-list` : 인기 테마 목록 조회

---

## 📌 특이사항

- 예약이 취소되면 대기 중인 예약이 자동으로 예약됩니다.
- 결제는 예약 등록 시 **토스페이(TossPay)** 를 통해 이루어집니다.

---