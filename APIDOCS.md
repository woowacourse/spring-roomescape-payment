# RoomEscape API 문서

## 인증 (AuthController)

### 로그인

- **POST** `/login`
- **설명**: 로그인 요청. 성공 시 access 토큰을 쿠키로 반환.
- **Request Body**:
  ```json
  {
    "email": "member@test.com",
    "password": "password123!"
  }
  ```
- **Response**: 200 OK, 쿠키에 access 토큰
- **Error Response**
    - 로그인 정보가 올바르지 않은 경우 : 400 BAD_REQUEST

### 로그인 체크

- **GET** `/login/check`
- **권한**: GENERAL
- **설명**: access 토큰을 통해 로그인 상태 및 프로필 정보 확인
- **Response**:
  ```json
  {
    "id": 1,
    "roleName": "GENERAL",
    "name": "홍길동"
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

### 로그아웃

- **POST** `/logout`
- **권한**: GENERAL
- **설명**: access 토큰 쿠키 삭제(로그아웃)
- **Response**: 200 OK, 쿠키 삭제
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

---

## 회원 (MemberController)

### 전체 회원 조회

- **GET** `/members`
- **권한**: ADMIN
- **설명**: 모든 회원의 프로필 정보 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "roleName": "GENERAL",
      "name": "홍길동"
    },
    {
      "id": 2,
      "roleName": "ADMIN",
      "name": "관리자"
    }
  ]
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

---

## 예약 (ReservationController)

### 전체 예약 조회

- **GET** `/reservations`
- **권한**: ADMIN
- **설명**: 모든 예약 정보 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "member": {
        "id": 1,
        "roleName": "GENERAL",
        "name": "홍길동"
      },
      "theme": {
        "id": 1,
        "name": "테마1"
      },
      "date": "2023-10-01",
      "time": "10:00"
    }
  ]
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

### 필터에 의한 예약 조회

- **GET** `/reservations?memberId=1&themeId=1&from=2023-10-01&to=2023-10-31`
- **권한**: ADMIN
- **설명**: 필터(회원, 테마, 기간)로 예약 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "member": {
        "id": 1,
        "roleName": "GENERAL",
        "name": "홍길동"
      },
      "theme": {
        "id": 1,
        "name": "테마1"
      },
      "date": "2023-10-01",
      "time": "10:00"
    }
  ]
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

### 내 예약 상태 조회

- **GET** `/reservations/state`
- **권한**: GENERAL
- **설명**: 내 예약 상태 정보 조회
- **Response**:
  ```json
  {
    "reservations": [
      {
        "id": 1,
        "theme": {
          "id": 1,
          "name": "테마1"
        },
        "date": "2023-10-01",
        "time": "10:00"
      }
    ]
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

### 관리자의 예약 생성

- **POST** `/reservations`
- **권한**: ADMIN
- **설명**: 관리자가 특정 회원의 예약 생성
- **Request Body**:
  ```json
  {
    "memberId": 1,
    "themeId": 1,
    "date": "2023-10-01",
    "timeId": 1
  }
  ```
- **Response**: 201 Created
  ```json
  {
    "id": 1,
    "member": {
      "id": 1,
      "roleName": "GENERAL",
      "name": "홍길동"
    },
    "theme": {
      "id": 1,
      "name": "테마1"
    },
    "date": "2023-10-01",
    "time": "10:00"
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 중복되 예약을 생성하는 경우 : 400 BAD_REQUEST
    - 과거 에약을 생성하는 경우 : 400 BAD_REQUEST

### 회원의 예약 생성

- **POST** `/reservations/mine`
- **권한**: GENERAL
- **설명**: 회원이 자신의 예약 생성
- **Request Body**:
  ```json
  {
    "themeId": 1,
    "date": "2023-10-01",
    "timeId": 1
  }
  ```
- **Response**: 201 Created
  ```json
  {
    "id": 1,
    "member": {
      "id": 1,
      "roleName": "GENERAL",
      "name": "홍길동"
    },
    "theme": {
      "id": 1,
      "name": "테마1"
    },
    "date": "2023-10-01",
    "time": "10:00"
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 중복되 예약을 생성하는 경우 : 400 BAD_REQUEST
    - 과거 예약을 생성하는 경우 : 400 BAD_REQUEST
    - 결제 승인에 실패하는 경우 : 400 BAD_REQUEST

### 예약 삭제

- **DELETE** `/reservations/{reservationId}`
- **권한**: ADMIN
- **설명**: 예약 삭제
- **Response**: 204 No Content
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 삭제할 예약이 존재하지 않는 경우 : 404 NOT_FOUND

---

## 예약 시간 (ReservationTimeController)

### 전체 예약 시간 조회

- **GET** `/times`
- **설명**: 모든 예약 시간 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "time": "10:00"
    },
    {
      "id": 2,
      "time": "11:00"
    }
  ]
  ```

### 테마/날짜별 예약 시간 및 예약 여부 조회

- **GET** `/times?themeId=1&date=2023-10-01`
- **설명**: 특정 테마와 날짜의 예약 시간 및 예약 여부 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "time": "10:00",
      "isBooked": true
    },
    {
      "id": 2,
      "time": "11:00",
      "isBooked": false
    }
  ]
  ```

### 예약 시간 추가

- **POST** `/times`
- **권한**: ADMIN
- **설명**: 예약 시간 추가
- **Request Body**:
  ```json
  {
    "time": "12:00"
  }
  ```
- **Response**: 201 Created
  ```json
  {
    "id": 3,
    "time": "12:00"
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 중복된 예약 시간인 경우 : 400 BAD_REQUEST

### 예약 시간 삭제

- **DELETE** `/times/{reservationTimeId}`
- **권한**: ADMIN
- **설명**: 예약 시간 삭제
- **Response**: 204 No Content
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 삭제할 예약 시간이 존재하지 않는 경우 : 404 NOT_FOUND
    - 예약 시간에 대한 예약과 대기가 이미 존재하는 경우 : 400 BAD_REQUEST

---

## 테마 (ThemeController)

### 전체 테마 조회

- **GET** `/themes`
- **설명**: 모든 테마 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "테마1",
      "description": "테마1 설명"
    },
    {
      "id": 2,
      "name": "테마2",
      "description": "테마2 설명"
    }
  ]
  ```

### 인기 테마 조회

- **GET** `/themes/ranking?size=3`
- **설명**: 최근 7일간 인기 테마 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "테마1",
      "description": "테마1 설명"
    },
    {
      "id": 2,
      "name": "테마2",
      "description": "테마2 설명"
    }
  ]
  ```

### 테마 추가

- **POST** `/themes`
- **권한**: ADMIN
- **설명**: 테마 추가
- **Request Body**:
  ```json
  {
    "name": "테마3",
    "description": "테마3 설명"
  }
  ```
- **Response**: 201 Created
  ```json
  {
    "id": 3,
    "name": "테마3",
    "description": "테마3 설명"
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

### 테마 삭제

- **DELETE** `/themes/{id}`
- **권한**: ADMIN
- **설명**: 테마 삭제
- **Response**: 204 No Content
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 존재하지 않는 테마를 삭제하려고 하는 경우 : 404 NOT_FOUND
    - 테마에 대한 예약과 대기가 이미 존재하는 경우 : 400 BAD_REQUEST

---

## 대기 (WaitingController)

### 전체 대기 조회

- **GET** `/waiting`
- **권한**: ADMIN
- **설명**: 모든 대기 정보 조회
- **Response**:
  ```json
  [
    {
      "id": 1,
      "member": {
        "id": 1,
        "roleName": "GENERAL",
        "name": "홍길동"
      },
      "theme": {
        "id": 1,
        "name": "테마1"
      },
      "date": "2023-10-01",
      "time": "10:00"
    }
  ]
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN

### 대기 추가

- **POST** `/waiting`
- **권한**: GENERAL
- **설명**: 대기 신청
- **Request Body**:
  ```json
  {
    "themeId": 1,
    "date": "2023-10-01",
    "timeId": 1
  }
  ```
- **Response**: 201 Created
  ```json
  {
    "id": 1,
    "member": {
      "id": 1,
      "roleName": "GENERAL",
      "name": "홍길동"
    },
    "theme": {
      "id": 1,
      "name": "테마1"
    },
    "date": "2023-10-01",
    "time": "10:00"
  }
  ```
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 중복된 예약을 생성하는 경우 : 400 BAD_REQUEST
    - 결제 승인에 실패하는 경우 : 400 BAD_REQUEST
    - 대기를 추가할 예약이 존재하지 않는 경우 : 400 BAD_REQUEST
    - 과거 날짜와 시간으로 대기를 생성하는 경우 : 400 BAD_REQUEST

### 대기 삭제

- **DELETE** `/waiting/{id}`
- **권한**: GENERAL
- **설명**: 대기 삭제
- **Response**: 204 No Content
- **Error Response**
    - 쿠키가 존재하지 않는 경우 : 404 NOT_FOUND
    - access 토큰이 올바르지 않은 경우 : 401 UNAUTHORIZED
    - 권한이 맞지 않는 경우 : 403 FORBIDDEN
    - 삭제할 대기가 존재하지 않는 경우 : 404 NOT_FOUND
