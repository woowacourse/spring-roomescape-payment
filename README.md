# [ERD](https://www.erdcloud.com/d/i7o7W64kLhNj9TW5m)

<img width="1218" src="https://github.com/ChooSeoyeon/spring-roomescape-payment/assets/83302344/d46b96ef-b589-433c-90dd-5294d3f499f6">

# 기능 명세

## 1단계 - 에약 시 결제 단계 추가

- [x] 결제창 UI 추가
    - [x] 결제창에서 결제수단을 선택하고 토스페이먼츠에게 결제 요청을 함 [2]
    - [x] 서버에게 결제정보를 전달함 [3]
- [x] 토스페이먼츠에게 결제승인 API를 호출함 [4]
    - 사용자 예약 추가 API Request에 결제승인을 위한 정보(paymentKey, orderId) 추가
- [x] 결제승인 API 호출에 실패 한 경우, 에러 핸들링 [4]

## 2단계 - 내 예약 페이지 변경

- [x] 결제 테이블 추가
    - 결제 정보: paymentKey, type, orderId, orderName, currency, method, totalAmount, status
- [x] 사용자 예약 추가 시 결제 정보를 DB에 저장
- [x] 내 예약 조회 API Response에 결제 정보 추가
    - 결제 정보: paymentKey, currency, totalAmount, status

## 3단계 - 배포하기

- [x] WAS 서버 배포

## 4단계 - 문서화

- [x] ERD 작성
- [x] RestDocs 적용
- [ ] Logger 추가

## 5단계 - 리팩토링

- [ ] 대기 테이블과 예약 테이블 간 연관관계 제거
    - 예약결제(결제정보, 예약, 회원)
    - 예약(예약정보, 회원)
    - 대기(예약정보, 회원)
- [ ] 예약, 결제 플로우에 soft delete 적용(삭제 -> 취소)
    - 예약상태에 '예약취소' 값 추가
    - 하나의 예약에 여러 회원의 결제 정보 존재 가능
- [ ] LazyLoading과 OSIV 적용 여부 고민
- [ ] 테마 별 금액 적용
- [ ] DB 서버 배포
- [ ] 배포 스크립트 작성
- [ ] StopLight로 렌더링

### 결제 플로우

- [1] 클라이언트가 토스페이먼츠에게 결제위젯 렌더 요청
- [2] 클라이언트가 토스페이먼츠에게 결제수단 선택 및 결제 요청
- [3] 클라이언트가 서버에게 결제정보를 전달
- [4] 서버가 토스페이먼츠에게 결제승인 API[외부 API]  호출-> API 호출 실패 시, 에러 핸들링
- [5] 토스페이먼츠가 클라이언트에게 결제승인 결과 안내

# 팀 컨벤션

- DTO
    - 직렬화/역직렬화 대상이 되는 DTO는 class로 구현한다
        - 네이밍 컨벤션: xxxRequest, xxxResponse
        - 목적: 직렬화/역직렬화에 필요한 코드만 제공하며 동작 원리 학습
    - 직렬화/역직렬화 대상이 되지 않는 DTO는 record로 구현한다
        - 네이밍 컨벤션: xxxInput, xxxOutput

# 고민

- 예약 정보가 삭제된 결제 정보 괜찮은가..?

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
GET /reservations/mine
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
  "timeId": 1,
  "paymentKey": "testPaymentKey",
  "orderId": "testOrderId",
  "amount": 1
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
