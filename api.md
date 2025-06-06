# api 명세서

# 사용자

---

## 로그인
### GET /login 
## Request
```json
{
email:"user@user.com",
password:"password"
}
```
---

## 예약하기
### POST /reservations
## Request

```json
{
  "date": "2025-06-15",
  "timeId": 3,
  "themeId": 5,
  "paymentKey": "pay_abc123xyz",
  "orderId": "order_20250615_001",
  "amount": 32000
}
```

## Response
```json
{
    "id": 1001,
    "date": "2025-06-15",
    "time": {
      "id": 1,
      "startAt": "11:00"
    },
    "theme": {
      "id": 10,
      "name": "유령의 저택",
      "description": "깊은 밤의 저택에서 탈출하라",
      "thumbnail": "https://example.com/thumbnails/ghost-mansion.jpg"
    },
    "member": {
      "id": 20,
      "name": "홍길동"
    },
    "status": "예약 완료"
  }
```

## 자신의 예약 확인하기
### GET /reservations/mine
## Response
```json
[
  {
    "id": 1001,
    "date": "2025-06-15",
    "time": {
      "id": 1,
      "startAt": "11:00"
    },
    "theme": {
      "id": 10,
      "name": "유령의 저택",
      "description": "깊은 밤의 저택에서 탈출하라",
      "thumbnail": "https://example.com/thumbnails/ghost-mansion.jpg"
    },
    "member": {
      "id": 20,
      "name": "홍길동"
    },
    "status": "예약"
  },
  {
    "id": 1002,
    "date": "2025-06-16",
    "time": {
      "id": 2,
      "startAt": "13:00"
    },
    "theme": {
      "id": 11,
      "name": "타임 루프",
      "description": "같은 하루를 반복하는 세계",
      "thumbnail": "https://example.com/thumbnails/time-loop.jpg"
    },
    "member": {
      "id": 21,
      "name": "김민지"
    },
    "status": "예약"
  }
]
```

## 예약 가능한 시간 목록 확인하기
## GET /available-times
## Request
```json
{
  "date": "2025-06-15",
  "themeId": 5
}
```
## Response
```json
[
  {
    "timeId": 1,
    "startAt": "10:00",
    "alreadyBooked": false
  },
  {
    "timeId": 2,
    "startAt": "12:00",
    "alreadyBooked": true
  },
  {
    "timeId": 3,
    "startAt": "14:00",
    "alreadyBooked": false
  }
]
```


## 대기 생성하기
## POST /waitings
## Requset
```json
{
  "date": "2025-06-15",
  "timeId": 3,
  "themeId": 5
}
```

## Response
```json
  {
    "id": 101,
    "date": "2025-06-20",
    "time": {
      "id": 4,
      "startAt": "14:00"
    },
    "theme": {
      "id": 6,
      "name": "미로 속 진실",
      "description": "끝없이 이어지는 미궁을 탈출하라",
      "thumbnail": "https://example.com/thumbnails/theme6.jpg"
    },
    "member": {
      "id": 12,
      "name": "홍길동"
    },
    "createdAt": "2025-06-01T15:30:45"
  }
```
## 대기 취소하기
## DELETE /waitings/{id}


# 관리자

## 예약 생성
## POST /admin/reservations
## Requset
```json
{
  "date": "2025-06-15",
  "timeId": 3,
  "themeId": 7,
  "memberId": 15
}
```
## 예약 삭제
## DELETE /admin/reservations/{id}


## 전체 예약 보기
## GET /admin/reservations

## 조건별 예약 목록 조회
### GET /admin/reservations/filtered
#### Query Parameter
- themeId (Long): 테마 ID
- memberId (Long): 회원 ID
- dateFrom (yyyy-MM-dd): 시작 날짜
- dateTo (yyyy-MM-dd): 종료 날짜

#### 예시
```
/admin/reservations/filtered?themeId=2&memberId=3&dateFrom=2024-07-01&dateTo=2024-07-31
```
#### Response
```json
[
  {
    "id": 10,
    "date": "2024-07-01",
    "time": {
      "id": 1,
      "startAt": "10:00"
    },
    "theme": {
      "id": 2,
      "name": "방탈출 테마명",
      "description": "테마 설명",
      "thumbnail": "https://..."
    },
    "member": {
      "id": 3,
      "name": "홍길동"
    },
    "status": "예약"
  }
]
```

## 예약 상태 목록 조회
### GET /admin/reservations/statuses
#### Response
```json
[
  {
    "id": "BOOKED",
    "name": "예약"
  },
  {
    "id": "PAID",
    "name": "결제 완료"
  }
]
```

## 예약 대기 생성
### POST /admin/waitings
#### Request
```json
{
  "date": "2024-07-01",      // 예약 날짜 (yyyy-MM-dd)
  "timeId": 1,                 // 예약 시간 ID
  "themeId": 2,                // 테마 ID
  "memberId": 3                // 회원 ID
}
```
#### Response
```json
{
  "id": 101,
  "date": "2024-07-01",
  "time": {
    "id": 1,
    "startAt": "10:00"
  },
  "theme": {
    "id": 2,
    "name": "방탈출 테마명",
    "description": "테마 설명",
    "thumbnail": "https://..."
  },
  "member": {
    "id": 3,
    "name": "홍길동"
  },
  "createdAt": "2024-06-30T15:30:45"
}
```

## 예약 대기 삭제
### DELETE /admin/waitings/{id}
- Path Variable: id (Long) 예약 대기 ID
- Response: 204 No Content

## 예약 대기 목록 조회
### GET /admin/waitings
#### Response
```json
[
  {
    "id": 101,
    "date": "2024-07-01",
    "time": {
      "id": 1,
      "startAt": "10:00"
    },
    "theme": {
      "id": 2,
      "name": "방탈출 테마명",
      "description": "테마 설명",
      "thumbnail": "https://..."
    },
    "member": {
      "id": 3,
      "name": "홍길동"
    },
    "status": "1번째 예약 대기"
  },
  {
    "id": 102,
    "date": "2024-07-01",
    "time": {
      "id": 1,
      "startAt": "10:00"
    },
    "theme": {
      "id": 2,
      "name": "방탈출 테마명",
      "description": "테마 설명",
      "thumbnail": "https://..."
    },
    "member": {
      "id": 4,
      "name": "김철수"
    },
    "status": "2번째 예약 대기"
  }
]
```

## 예약 시간 생성
### POST /times
#### Request
```json
{
  "startAt": "10:00"   // 시작 시간 (HH:mm)
}
```
#### Response
```json
{
  "id": 1,
  "startAt": "10:00"
}
```

## 예약 시간 삭제
### DELETE /times/{id}
- Path Variable: id (Long) 예약 시간 ID
- Response: 204 No Content

## 예약 시간 목록 조회
### GET /times
#### Response
```json
[
  {
    "id": 1,
    "startAt": "10:00"
  },
  {
    "id": 2,
    "startAt": "11:00"
  }
]
```
