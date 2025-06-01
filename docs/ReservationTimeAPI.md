### 예약 가능 시간 조회

**GET /times/{테마 ID}/available**

Request Parameter

```text
date : 조회할 날짜 (YYYY-MM-DD)
```

Response Body

```json
[
  {
    "id": 1,
    "startAt": "10:00",
    "alreadyBooked": false
  },
  {
    "id": 2,
    "startAt": "11:00",
    "alreadyBooked": true
  }
]
```

### 예약 시간 전체 조회

**GET /times**

Response Body

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

### 예약 시간 추가 (어드민)

**POST /admin/times**

Request Body

```json
{
  "startAt": "12:00"
}
```

Response Body

```json
{
  "id": 3,
  "startAt": "12:00"
}
```

### 예약 시간 삭제 (어드민)

**DELETE /admin/times/{예약 시간 ID}**
