# 기능 명세

## 1단계 - 에약 시 결제 단계 추가

- [ ] 사용자가 날짜, 테마, 시간을 선택하고 결제를 해야 예약할 수 있도록 변경
- [ ] 결제 기능에 외부의 결제 API 연동
- [ ] 결제 승인 API 호출에 실패 한 경우, 에러 핸들링
    - 사용자는 예약 실패 시, 결제 실패 사유를 알 수 있음
- [ ] 결제창 UI 추가
- [ ] 사용자 예약 추가 API Request에 결제승인을 위한 정보(paymentKey, orderId) 추가

# API 명세

## 예외

Response

```
{
  "message": "토큰이 유효하지 않습니다."
}
```

## 인증

### 로그인 API

Request

```
POST /login
Content-Type: application/json

{
  "password": "password",
  "email": "admin@email.com"
}
```

Response

```
HTTP/1.1 200 OK
Content-Type: application/json
Set-Cookie: token=hello.example.token; Path=/; HttpOnly
```

### 로그아웃 API

Request

```
POST /logout
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 200 OK
```

### 인증 정보 조회 API

Request

```
GET /login/check
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "name": "어드민"
}
```

### 회원가입 API

Request

```
POST /signup
Content-Type: application/json

{
  "email": "admin@email.com",
  "password": "password",
  "name": "어드민"
}
```

Response

```
HTTP/1.1 201 OK
Content-Type: application/json

{
  "id": 1,
  "name": "브라운",
  "email": "admin@email.com"
}
```

## 사용자

### 사용자 목록 조회 API (접근 권한: 관리자)

Request

```
GET /members
```

Response

```
HTTP/1.1 200
Cookie: token=hello.example.token
Content-Type: application/json

{
  "members": [
    {
      "id": 1,
      "name": "관리자",
      "email": "admin@gmail.com"
    }
  ]
}
```

## 예약

### 예약 목록 조회 API (접근 권한: 관리자)

Request

```
GET /reservations?themeId={$}&memberId={$}&dateFrom={$}&dateTo={$}
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 200
Content-Type: application/json

{
  "reservations": [
    {
      "id": 1,
      "member": {
        "id": 1,
        "name": "사용자",
        "email": "user@gmail.com"
      },
      "date": "2024-08-05",
      "time": {
        "id": 1,
        "startAt": "10:00"
      },
      "theme": {
        "id": 1,
        "name": "레벨2 탈출",
        "description": "우테코 레벨2를 탈출하는 내용입니다.",
        "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
      }
    }
  ]
}
```

### 내 예약 목록 조회 API

Request

```
GET /reservations-mine
cookie: token=hello.example.token
```

Response

```
HTTP/1.1 200 
Content-Type: application/json

{
  "reservations": [
    {
      "reservationId": 1,
      "theme": "레벨2 탈출",
      "date": "2024-08-05",
      "time": "10:00",
      "status": "예약"
    }
  ]
}
```

### 예약 추가 API

Request

```
POST /reservations
Cookie: token=hello.example.token
Content-Type: application/json

{
  "date": "2024-08-06",
  "themeId": 1,
  "timeId": 1
}
```

Response

```
HTTP/1.1 201
Content-Type: application/json

{
  "id": 2,
  "member": {
    "id": 1,
    "name": "사용자",
    "email": "user@gmail.com"
  },
  "date": "2024-08-06",
  "time": {
    "id": 1,
    "startAt": "10:00"
  },
  "theme": {
    "id": 1,
    "name": "레벨2 탈출",
    "description": "우테코 레벨2를 탈출하는 내용입니다.",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
  }
}
```

### 관리자용 예약 추가 API (접근 권한: 관리자)

Request

```
POST /admin/reservations
Cookie: token=hello.example.token
Content-Type: application/json

{
  "date": "2024-08-07",
  "themeId": 1,
  "timeId": 1,
  "memberId": 1
}
```

Response

```
HTTP/1.1 201
Content-Type: application/json

{
  "id": 3,
  "member": {
    "id": 1,
    "name": "사용자",
    "email": "user@gmail.com"
  },
  "date": "2024-08-07",
  "time": {
    "id": 1,
    "startAt": "10:00"
  },
  "theme": {
    "id": 1,
    "name": "레벨2 탈출",
    "description": "우테코 레벨2를 탈출하는 내용입니다.",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
  }
}
```

### 예약 삭제 API (접근 권한: 관리자)

Request

```
DELETE /reservations/1?memberId=1
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 204
```

## 예약 대기

### 예약 대기 목록 조회 API (접근 권한: 관리자)

Request

```
GET /reservations/waitings
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 200 
Content-Type: application/json

{
  "waitings": [
    {
      "id": 1,
      "name": "사용자",
      "theme": "레벨2 탈출",
      "date": "2024-08-05",
      "startAt": "10:00",
      "reservationId": 1
    }
  ]
}
```

### 예약 대기 추가 API

Request

```
POST /reservations/waitings
Cookie: token=hello.example.token
Content-Type: application/json

{
  "date": "2023-08-05",
  "themeId": 1,
  "timeId": 1
}
```

Response

```
HTTP/1.1 201
Content-Type: application/json

{
  "id": 1,
  "name": "사용자",
  "theme": "레벨2 탈출",
  "date": "2024-08-05",
  "startAt": "10:00",
  "reservationId": 1
}
```

### 예약 대기 삭제 API

Request

```
DELETE /reservations/1/waitings
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 204
```

### 관리자용 예약 대기 삭제 API (접근 권한: 관리자)

Request

```
DELETE /admin/reservations/waitings/1
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 204
```

## 예약 시간

### 예약 시간 목록 조회 API (접근 권한: 관리자)

Request

```
GET /times
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 200 
Content-Type: application/json

{
  "times": [
    {
      "id": 1,
      "startAt": "10:00"
    }
  ]
}
```

### 예약 가능 시간 목록 조회 API

Request

```
GET /times/available?date={&}&timeId={$}
```

Response

```
HTTP/1.1 200 
Content-Type: application/json

{
  "times": [
    {
      "id": 1,
      "startAt": "10:00",
      "alreadyBooked": false
    }
  ]
}
```

### 예약 시간 추가 API (접근 권한: 관리자)

Request

```
POST /times
Cookie: token=hello.example.token
Content-Type: application/json

{
  "startAt": "10:00"
}
```

Response

```
HTTP/1.1 201
Content-Type: application/json

{
  "id": 2,
  "startAt": "11:00"
}
```

### 예약 시간 삭제 API (접근 권한: 관리자)

Request

```
DELETE /times/1
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 204
```

## 테마

### 테마 목록 조회 API

Request

```
GET /themes
```

Response

```
HTTP/1.1 200 
Content-Type: application/json

{
  "themes": [
    {
      "id": 1,
      "name": "레벨2 탈출",
      "description": "우테코 레벨2를 탈출하는 내용입니다.",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    }
  ]
}
```

### 인기 테마 목록 조회 API

Request

```
GET /themes/popular
```

Response

```
HTTP/1.1 200
Content-Type: application/json

{
  "themes": [
    {
      "id": 1,
      "name": "레벨2 탈출",
      "description": "우테코 레벨2를 탈출하는 내용입니다.",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    }
  ]
}
```

### 테마 추가 API (접근 권한: 관리자)

Request

```
POST /themes
Cookie: token=hello.example.token
Content-Type: application/json

{
  "name": "레벨2 탈출",
  "description": "우테코 레벨2를 탈출하는 내용입니다.",
  "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
}
```

Response

```
HTTP/1.1 201
Location: /themes/1
Content-Type: application/json

{
  "id": 1,
  "name": "레벨2 탈출",
  "description": "우테코 레벨2를 탈출하는 내용입니다.",
  "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
}

```

### 테마 삭제 API (접근 권한: 관리자)

Request

```
DELETE /themes/1
Cookie: token=hello.example.token
```

Response

```
HTTP/1.1 204
```
