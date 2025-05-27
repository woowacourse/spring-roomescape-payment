# 방탈출 예약 시스템

이 프로젝트는 **방탈출 테마 예약 서비스**의 백엔드 시스템입니다.  
주요 기능, API 명세, 예외 처리, 인증/인가 정책 등을 설명합니다.

---

## ✅ 주요 기능 체크리스트

### 예외 처리

- [x] 전역 예외 처리 및 상황별 HTTP 상태코드 반환 (404, 403 등)
- [x] 예약 예외: 시간, 날짜, 예약자명 필수, 예약 일시는 현재 시각 이후만 허용
- [x] 시간/테마 예외: 중복 등록 방지, 예약이 있는 아이템은 삭제 불가

### 예약 기능

- [x] 예약 생성: 같은 시간+날짜 중복 예약 불가, 로그인 사용자/관리자 예약
- [x] 예약 조회: 날짜/테마별로 예약 가능 시간 확인, 사용자별 예약 목록 조회
- [x] 관리자 예약 검색: 예약자/테마/날짜 기준 검색
- [x] 예약 대기: 대기 등록/취소, 중복 대기 방지, 대기 순번 자동 승인

### 결제 기능
- [ ] 예약을 할려면 토스 결제 API를 통해 결제를 진행해야 한다.
- [ ] API 호출이 되지 않은 경우 상황에 맞는 예외 처리
- [ ] 결제 실패 시 예외 사유를 맞게 반환한디.

### 테마/시간 관리

- [x] 테마/시간 전체 조회, 추가, 삭제 (예약 시 삭제 제한)
- [x] 인기 테마: 최근 7일간 상위 10개 인기 테마 제공

### 사용자 기능

- [x] 회원가입/로그인/정보 관리
- [x] JWT 토큰 기반 인증/인가
- [x] ADMIN/USER 역할 구분, 어드민 경로 보호

---

## 📖 API 명세서

### 1. 멤버(Member)

- [x] 회원가입  
  `POST /members`
    - **Request**
      ```json
      {
        "email": "string",
        "password": "string",
        "name": "string"
      }
      ```
    - **Response**
      ```json
      {
        "id": 1,
        "email": "string",
        "name": "string"
      }
      ```
- [x] 전체 조회 (어드민)  
  `GET /admin/members`

---

### 2. 인증(Authentication)

- [x] 로그인  
  `GET /login` (폼)  
  `POST /login`
    - **Request**
      ```json
      {
        "email": "string",
        "password": "string"
      }
      ```
    - **Response**  
      성공 시 Cookie에 JWT 토큰(`token`) 반환

- [x] 로그아웃  
  `POST /logout`

- [x] 로그인 검사  
  `GET /login/check`  
  쿠키 내 Token 키를 통해 인증 여부 확인

---

### 3. 예약(Reservation)

- [x] 예약 생성 (유저)  
  `POST /reservations`
    - **Request**
      ```json
      {
        "date": "YYYY-MM-DD",
        "timeId": 1,
        "themeId": 1
      }
      ```
    - **Response**
      ```json
      {
        "id": 1,
        "member": {...},
        "date": "YYYY-MM-DD",
        "time": {...},
        "theme": {...}
      }
      ```
- [x] 예약 생성 (어드민)  
  `POST /admin/reservations`
    - **Request**
      ```json
      {
        "date": "YYYY-MM-DD",
        "timeId": 1,
        "themeId": 1,
        "memberId": 1
      }
      ```
- [x] 예약 조건 조회 (어드민)  
  `GET /admin/reservations?themeId=&memberId=&from=&to=`
- [x] 나의 예약 조회  
  `GET /reservations/me`  
  (예약 배열 반환)
- [x] 예약 삭제 (어드민)  
  `DELETE /admin/reservations/{id}`
- [x] 예약 삭제 (유저)  
  `DELETE /reservations/{id}`

---

### 4. 예약 대기(Waiting)

- [x] 예약 대기 전체 조회 (어드민)  
  `GET /admin/reservations/waitings`
- [x] 대기 생성 (유저)  
  `POST /reservations/waiting`
- [x] 대기 취소 (유저)  
  `DELETE /reservations/{id}`

---

### 5. 테마(Theme)

- [x] 테마 전체 조회  
  `GET /themes`
- [x] 테마 추가  
  `POST /admin/themes`
- [x] 테마 삭제  
  `DELETE /admin/themes/{id}`
- [x] 인기 테마 조회  
  `/` 요청 시 최근 7일간 상위 10개 인기 테마 포함

---

### 6. 시간(Time)

- [x] 시간 전체 조회  
  `GET /times`
- [x] 시간 추가  
  `POST /admin/times`
- [x] 시간 삭제  
  `DELETE /admin/times/{id}`

---

## 🔐 인증/인가 정책

- [x] JWT 토큰을 이용한 인증 (쿠키 기반)
- [x] 어드민 경로(`/admin/**`)는 ADMIN 권한만 접근 가능
- [x] 예약 기능은 로그인 사용자만 가능

---
