### 내 예약 조회

**GET /reservations**

Response Body

```json
[
  {
    "reservationId": 1,
    "theme": "어드벤처 방탈출",
    "date": "2024-07-16",
    "time": "14:00",
    "status": "예약 완료",
    "priority": 1,
    "paymentKey": "paymentKey",
    "amount": 10000
  }
]
```

### 예약 추가

**POST /reservations**

Request Body

```json
{
  "date": "2024-07-20",
  "themeId": 1,
  "timeId": 1,
  "paymentKey": "paymentKey",
  "orderId": 1,
  "amount": 10000,
  "paymentType": "NORMAL"
}
```

Response Body

```json
{
  "id": 3,
  "name": "내이름",
  "date": "2024-07-20",
  "time": {
    "id": 1,
    "startAt": "12:00"
  },
  "theme": {
    "id": 1,
    "name": "공포",
    "description": "설명",
    "thumbnail": "썸네일"
  },
  "state": "예약 완료"
}
```

### 예약 대기 추가

**POST /reservations/pending**

Request Body

```json
{
  "date": "2024-07-20",
  "timeId": 1,
  "themeId": 1
}
```

Response Body

```json
{
  "id": 3,
  "name": "내이름",
  "date": "2024-07-20",
  "time": {
    "id": 1,
    "startAt": "12:00"
  },
  "theme": {
    "id": 1,
    "name": "공포",
    "description": "설명",
    "thumbnail": "썸네일"
  },
  "state": "예약 완료"
}
```

### 예약 삭제

**DELETE /reservations/{예약 ID}**

### 예약 추가 (어드민)

**POST /admin/reservations**

Request Body

```json
{
  "memberId": 1,
  "date": "2024-07-20",
  "themeId": 1,
  "timeId": 1
}
```

Response Body

```json
{
  "id": 3,
  "name": "내이름",
  "date": "2024-07-20",
  "time": {
    "id": 1,
    "startAt": "12:00"
  },
  "theme": {
    "id": 1,
    "name": "공포",
    "description": "설명",
    "thumbnail": "썸네일"
  },
  "state": "예약 완료"
}
```

### 필터링하여 전체 예약 조회 (어드민)

**GET /admin/reservations**

Request Parameter

```text
memberId : 회원 아이디 (선택)
themeId : 테마 아이디 (선택)
dateFrom : 시작 날짜 (선택, YYYY-MM-DD)
dateTo : 종료 날짜 (선택, YYYY-MM-DD)
```

Response Body

```json
[
  {
    "id": 3,
    "name": "내이름",
    "date": "2024-07-20",
    "time": {
      "id": 1,
      "startAt": "12:00"
    },
    "theme": {
      "id": 1,
      "name": "공포",
      "description": "설명",
      "thumbnail": "썸네일"
    },
    "state": "예약 완료"
  }
]
```

### 예약 대기 전체 조회 (어드민)

**GET /admin/reservations/pending**

Response Body

```json
[
  {
    "id": 3,
    "member": {
      "memberId": 1,
      "name": "dompoo"
    },
    "theme": {
      "themeId": 1,
      "themeName": "공포"
    },
    "time": {
      "timeId": 1,
      "startAt": "12:00"
    },
    "date": "2024-07-20"
  }
]
```

### 예약 거절 (어드민)

**DELETE /admin/reservations/pending/{거절할 예약 ID}/deny**
