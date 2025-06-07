# API 명세

# View (Template Rendering)

## Admin View

| Method | URL                  | Description      | Template Name           |
|--------|----------------------|------------------|-------------------------|
| GET    | `/admin`             | 관리자 메인 페이지       | `admin/index`           |
| GET    | `/admin/reservation` | 예약 관리 페이지        | `admin/reservation-new` |
| GET    | `/admin/time`        | 시간 슬롯 관리 페이지     | `admin/time`            |
| GET    | `/admin/theme`       | 테마 관리 페이지        | `admin/theme`           |
| GET    | `/admin/waiting`     | 대기열 관리 페이지       | `admin/waiting`         |

---

## Member View

| Method | URL       | Description | Template Name |
|--------|-----------|-------------|---------------|
| GET    | `/login`  | 로그인 페이지     | `login`       |
| GET    | `/signup` | 회원가입 페이지    | `signup`      |

---

## Reservation View

| Method | URL                 | Description | Template Name      |
|--------|---------------------|-------------|--------------------|
| GET    | `/reservation`      | 예약 생성 페이지   | `reservation`      |
| GET    | `/reservation-mine` | 내 예약 목록 페이지 | `reservation-mine` |

# REST API (JSON)

로그인이 필요한 기능은 Cookie에 `token`이 포함되어 있어야 합니다. `token`은 로그인 후 발급받은 JWT입니다.

## 실패 응답

실패 응답은 알맞은 상태 코드와 함께 아래와 같은 형식으로 반환됩니다.


```json
{
  "message": "${errorMessage}"
}
```

## Admin Reservation API 명세

관리자가 예약을 생성, 삭제, 조회할 수 있는 API입니다.

### POST `/admin/reservations`

관리자가 예약을 생성합니다.

#### 요청 바디 예시

```json
{
  "date": "2025-06-05",
  "timeId": 1,
  "themeId": 2,
  "memberId": 1
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /reservations/1
```

---

### DELETE `/admin/reservations/{id}`

관리자가 특정 예약을 삭제합니다.

#### 요청 예시

```http
DELETE /admin/reservations/1 HTTP/1.1
Cookie: token={accessToken}
```

#### 응답

```http
HTTP/1.1 204 No Content
```

---

### GET `/admin/reservations`

관리자가 예약 목록을 조회합니다.

**선택 쿼리 파라미터 (필터링 가능)**

| 파라미터       | 타입       | 설명               |
|------------|----------|------------------|
| `themeId`  | `Long`   | 테마 ID로 필터링       |
| `memberId` | `Long`   | 회원 ID로 필터링       |
| `from`     | `String` | 시작일 (yyyy-MM-dd) |
| `to`       | `String` | 종료일 (yyyy-MM-dd) |

#### 요청 예시

```http
GET /admin/reservations?themeId=2&from=2025-06-01&to=2025-06-30 HTTP/1.1
Cookie: token={accessToken}
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "member": {
      "id": 1,
      "name": "홍길동"
    },
    "date": "2025-06-05",
    "time": {
      "id": 1,
      "startAt": "10:00"
    },
    "theme": {
      "id": 2,
      "name": "어둠의 방",
      "description": "어두운 테마",
      "thumbnail": "dark_theme.jpg"
    }
  }
]
```

---

## Admin Time API 명세

관리자가 시간 슬롯을 생성, 삭제할 수 있는 API입니다.

### POST `/admin/times`

관리자가 시간 슬롯을 생성합니다.

#### 요청 바디 예시

```json
{
  "startAt": "10:00"
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /times/1
```

---

### DELETE `/admin/times/{id}`

관리자가 특정 시간 슬롯을 삭제합니다.

#### 요청 예시

```http
DELETE /admin/times/1 HTTP/1.1
Cookie: token={accessToken}
```

#### 응답

```http
HTTP/1.1 204 No Content
```

---

## Admin Theme API 명세

관리자가 테마를 생성, 삭제할 수 있는 API입니다.

### POST `/admin/themes`

관리자가 테마를 생성합니다.

#### 요청 바디 예시

```json
{
  "name": "어둠의 방",
  "description": "어두운 테마",
  "thumbnail": "dark_theme.jpg"
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /themes/1
```

---

### DELETE `/admin/themes/{id}`

관리자가 특정 테마를 삭제합니다.

#### 요청 예시

```http
DELETE /admin/themes/1 HTTP/1.1
Cookie: token={accessToken}
```

#### 응답

```http
HTTP/1.1 204 No Content
```

---

## Admin Waiting API 명세

관리자가 대기열을 조회, 삭제할 수 있는 API입니다.

### GET `/admin/waitings`

관리자가 대기열 목록을 조회합니다.

#### 요청 예시

```http
GET /admin/waitings HTTP/1.1
Cookie: token={accessToken}
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "name": "홍길동",
    "theme": "어둠의 방",
    "date": "2025-06-05",
    "startAt": "10:00"
  },
  {
    "id": 2,
    "name": "김철수",
    "theme": "빛의 방",
    "date": "2025-06-05",
    "startAt": "11:00"
  }
]
```

---

### DELETE `/admin/waitings/{id}`

관리자가 특정 대기열 항목을 삭제합니다.

#### 요청 예시

```http
DELETE /admin/waitings/1 HTTP/1.1
Cookie: token={accessToken}
```

#### 응답

```http
HTTP/1.1 204 No Content
```

---

## 내 예약 조회 API

사용자의 예약과 대기열 정보를 결합해 날짜순으로 정렬된 예약 리스트를 조회합니다.

### GET `/reservations/mine`

내 예약 목록을 조회합니다.

#### 요청 예시

```http
GET /reservations/mine HTTP/1.1
Cookie: token={accessToken}
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "theme": "어둠의 방",
    "date": "2025-06-05",
    "time": "10:00",
    "status": "예약",
    "type": "RESERVE",
    "paymentKey": "pay_1234567890",
    "amount": 20000
  },
  {
    "id": 2,
    "theme": "빛의 방",
    "date": "2025-06-09",
    "time": "14:00",
    "status": "2번째 예약 대기",
    "type": "WAITING",
    "paymentKey": null,
    "amount": null
  }
]
```

---

## 개인 예약 생성 API

사용자가 예약을 생성합니다.

### POST `/reservations`

예약을 생성합니다.

#### 요청 바디 예시

```json
{
  "date": "2025-06-05",
  "timeId": 1,
  "themeId": 2,
  "paymentKey": "pay_1234567890",
  "orderId": "order_1234567890",
  "amount": 20000,
  "paymentType": "NORMAL"
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /reservations/1
```

---

### GET `/reservations`

예약 목록을 조회합니다.

#### 요청 예시

```http
GET /reservations HTTP/1.1
Cookie: token={accessToken}
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "member": {
      "id": 1,
      "name": "홍길동"
    },
    "date": "2025-06-05",
    "time": {
      "id": 1,
      "startAt": "10:00"
    },
    "theme": {
      "id": 2,
      "name": "어둠의 방",
      "description": "어두운 테마",
      "thumbnail": "dark_theme.jpg"
    }
  },
  {
    "id": 2,
    "member": {
      "id": 1,
      "name": "홍길동"
    },
    "date": "2025-06-09",
    "time": {
      "id": 2,
      "startAt": "14:00"
    },
    "theme": {
      "id": 3,
      "name": "빛의 방",
      "description": "밝은 테마",
      "thumbnail": "light_theme.jpg"
    }
  }
]
```

---

## 예약 가능 시간 조회 API

사용자가 예약 가능한 시간 슬롯을 조회합니다.

### GET `/times`

전체 시간 슬롯을 조회합니다.

#### 요청 예시

```http
GET /times HTTP/1.1
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "startAt": "10:00"
  },
  {
    "id": 2,
    "startAt": "11:00"
  },
  {
    "id": 3,
    "startAt": "12:00"
  }
]
```

---

### GET `/times/available`

특정 테마와 날짜에 예약 가능한 시간 슬롯을 조회합니다.

**필수 쿼리 파라미터**

| 파라미터    | 타입       | 설명               |
|---------|----------|------------------|
| `themeId` | `Long`   | 테마 ID            |
| `date`    | `String` | 조회할 날짜 (yyyy-MM-dd) |

#### 요청 예시

```http
GET /times/available?themeId=2&date=2025-06-05 HTTP/1.1
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "startAt": "10:00",
    "booked": false
  },
  {
    "id": 2,
    "startAt": "11:00",
    "booked": true
  }
]
```

---

## 테마 조회 API

사용자가 예약 가능한 테마를 조회합니다.

### GET `/themes`

전체 테마 목록을 조회합니다.

#### 요청 예시

```http
GET /themes HTTP/1.1
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "name": "어둠의 방",
    "description": "어두운 테마",
    "thumbnail": "dark_theme.jpg"
  },
  {
    "id": 2,
    "name": "빛의 방",
    "description": "밝은 테마",
    "thumbnail": "light_theme.jpg"
  }
]
```

---

### GET `/themes/rank`

예약 횟수 기준 테마 랭킹을 조회합니다.

#### 요청 예시

```http
GET /themes/rank HTTP/1.1
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "name": "어둠의 방",
    "description": "어두운 테마",
    "thumbnail": "dark_theme.jpg",
    "reservationCount": 100
  },
  {
    "id": 2,
    "name": "빛의 방",
    "description": "밝은 테마",
    "thumbnail": "light_theme.jpg",
    "reservationCount": 80
  }
]
```

---

## 예약 대기 API

특정 슬롯에 대해 대기를 신청합니다.

### POST `/reservations/wait`

예약 대기를 신청합니다.

#### 요청 바디 예시

```json
{
  "date": "2025-06-05",
  "themeId": 1,
  "timeId": 2
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /reservations/wait/1
```

---

### DELETE `/reservations/wait/{id}`

예약 대기를 취소합니다.

#### 요청 예시

```http
DELETE /reservations/wait/1 HTTP/1.1
Cookie: token={accessToken}
```

#### 응답

```http
HTTP/1.1 204 No Content
```

---

## 회원 관리 API

### POST `/members`

회원가입을 진행합니다.

#### 요청 바디 예시

```json
{
  "name": "홍길동",
  "email": "hong@email.com",
  "password": "password"
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /members/1
```

---

### GET `/members`

회원 목록을 조회합니다.

#### 요청 예시

```http
GET /members HTTP/1.1
Cookie: token={accessToken}
```

#### 응답 바디 예시

```json
[
  {
    "id": 1,
    "name": "홍길동"
  },
  {
    "id": 2,
    "name": "김철수"
  }
]
```

---

## 로그인 API

### POST `/login`

로그인을 진행합니다.

#### 요청 바디 예시

```json
{
  "email": "hong@email.com",
  "password": "password"
}
```

#### 응답

```http
HTTP/1.1 200 OK
Set-Cookie: token={accessToken}; HttpOnly; Secure; SameSite=Strict
```

---

### GET `/login/check`

로그인 상태를 확인합니다.

#### 요청 예시

```http
GET /login/check HTTP/1.1
Cookie: token={accessToken}
```

#### 응답 바디 예시

```json
{
  "name": "홍길동"
}
```

---

### POST `/logout`

로그아웃을 진행합니다.

#### 요청 예시

```http
POST /logout HTTP/1.1
Cookie: token={accessToken}
```

#### 응답

```http
HTTP/1.1 204 No Content
```

---

## 결제 전 검증 API

사용자의 결제 정보를 사전에 저장합니다.

### POST `/payments`

결제 정보를 사전 저장합니다.

#### 요청 바디 예시

```json
{
  "orderId": "order_1234567890",
  "amount": 20000
}
```

#### 응답

```http
HTTP/1.1 201 Created
Location: /payments/1
```
