# 사용자 예약 페이지 API

## Overview

| 기능           | HTTP Method | URL                                              | HTTP Status |
|--------------|-------------|--------------------------------------------------|-------------|
| 예약 생성        | POST        | /reservations                                    | 201         |
| 예약 대기 생성     | POST        | /reservations/wait                               | 201         |
| 테마 조회        | GET         | /themes                                          | 200         |
| 예약 가능한 시간 조회 | GET         | /times/available?date={date}&theme-id={theme-id} | 200         |

## 예약 생성

##### request

POST /reservations

```json
{
  "date": "2024-06-28",
  "themeId": "5",
  "timeId": "5",
  "paymentKey": "tgen_20240604171118jGsa4",
  "orderId": "JOANNAMC45MDg1MDcwMzk4ODMx",
  "paymentType": "NORMAL",
  "amount": 1000
}
```

##### response

201 CREATED

```json
{
  "id": 17,
  "member": {
    "id": 1,
    "name": "조조"
  },
  "date": "2024-06-28",
  "theme": {
    "id": 5,
    "name": "좀비 사태",
    "description": "좀비들의 공격",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
  },
  "time": {
    "id": 5,
    "startAt": "12:00"
  },
  "status": "SUCCESS"
}
```

## 예약 대기 생성

##### request

POST /reservations/wait

```json
{
  "date": "2024-06-09",
  "themeId": "5",
  "timeId": "1"
}
```

##### response

201 CREATED

```json
{
  "id": 17,
  "member": {
    "id": 2,
    "name": "어드민"
  },
  "date": "2024-06-09",
  "theme": {
    "id": 5,
    "name": "좀비 사태",
    "description": "좀비들의 공격",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
  },
  "time": {
    "id": 1,
    "startAt": "10:00"
  },
  "status": "WAIT"
}
```

## 테마 조회

##### request

GET /themes

##### response

200 OK

```json
{
  "resources": [
    {
      "id": 1,
      "name": "공포",
      "description": "무서워요",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    },
    {
      "id": 2,
      "name": "SF",
      "description": "미래",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    },
    {
      "id": 3,
      "name": "원숭이 사원",
      "description": "원숭이들의 공격",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    },
    {
      "id": 4,
      "name": "나가야 산다",
      "description": "빨리 탈출",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    },
    {
      "id": 5,
      "name": "좀비 사태",
      "description": "좀비들의 공격",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    }
  ]
}
```

## 예약 가능한 시간 조회

##### request

GET /times/available?date=2024-06-21&theme-id=4

##### response

200 OK

```json
{
  "resources": [
    {
      "id": 1,
      "startAt": "10:00",
      "booked": false
    },
    {
      "id": 2,
      "startAt": "10:30",
      "booked": false
    },
    {
      "id": 3,
      "startAt": "11:00",
      "booked": false
    },
    {
      "id": 4,
      "startAt": "11:30",
      "booked": false
    },
    {
      "id": 5,
      "startAt": "12:00",
      "booked": false
    }
  ]
}
```


