
---
**GET /themes**

**설명**  
모든 테마 정보를 조회한다.

**요청**

- HTTP Method: GET
- Query Parameters: 없음
- Request Body: 없음

**응답**

- HTTP Status: 200 OK
- Content-Type: application/json
- 응답 Body:
```json
[
  {
    "id": 1,
    "name": "공포의 저택",
    "description": "귀신이 사는 저택을 탈출하라!",
    "thumbnail": "https://example.com/images/theme1.png"
  },
  {
    "id": 2,
    "name": "시간의 미궁",
    "description": "타임머신을 타고 미래로...",
    "thumbnail": "https://example.com/images/theme2.png"
  }
]
```
---
**GET /reservations/times**

**설명**  
특정 날짜와 테마에 대한 모든 예약 가능 시간과 예약 여부를 조회한다.

**요청**

- HTTP Method: GET
- Query Parameters:
  - date (string, required): 조회할 날짜 (예: 2025-06-07)
  - themeId (integer, required): 조회할 테마 ID (예: 1)
- Request Example:
```
/reservations/times?date=2025-06-07&themeId=1
```

**응답**

- HTTP Status: 200 OK
- Content-Type: application/json
- 응답 Body:
```json
[
  {
    "startAt": "10:00:00",
    "timeId": 101,
    "isBooked": false
  },
  {
    "startAt": "12:00:00",
    "timeId": 102,
    "isBooked": true
  }
]
```
---
**POST /reservations/wait**

**설명**  
예약 대기를 생성한다.
인증을 위해 쿠키에 회원 정보를 담은 JWT가 포함되어야 한다.

**요청**

- HTTP Method: POST
- Content-Type: application/json
- Request Body:
```json
{
  "date": "2025-06-07",
  "timeId": 101,
  "themeId": 1
}
```

**응답**

- HTTP Status: 200 OK
- Content-Type: application/json
- 응답 Body:
```json
{
  "id": 5001,
  "memberInfo": {
    "id": 100,
    "name": "홍길동",
    "email": "gildong@example.com",
    "role": "USER"
  },
  "date": "2025-06-07",
  "time": {
    "startAt": "10:00:00",
    "timeId": 101,
    "isBooked": false
  },
  "theme": {
    "id": 1,
    "name": "공포의 저택",
    "description": "귀신이 사는 저택을 탈출하라!",
    "thumbnail": "https://example.com/images/theme1.png"
  }
}
```
---
**POST /payments**

**설명**  
결제 검증 정보를 저장한다.
토스페이먼츠에 결제 요청을 하기 전 반드시 같은 값으로 해당 API 요청을 수행하여야한다.
인증을 위해 쿠키에 회원 정보를 담은 JWT가 포함되어야 한다.

**요청**

- HTTP Method: POST
- Content-Type: application/json
- Request Body:
```json
{
  "orderId": "ORD123456789",
  "amount": 32000
}
```

**응답**

- HTTP Status: 200 OK
- Content-Type: application/json
- 응답 Body:
```json
{
  "orderId": "ORD123456789",
  "amount": 32000,
  "memberInfo": {
    "id": 100,
    "name": "홍길동",
    "email": "gildong@example.com",
    "role": "USER"
  }
}
```
---
**POST /reservations**

**설명**  
예약과 결제 확인을 동시에 처리하여 예약을 생성한다.
`paymentConfirm`에 들어갈 내용은 반드시 토스페이먼츠 결제 요청 후 리다이렉트 된 successUrl의 쿼리 파라미터 데이터를 사용하여야한다.
인증을 위해 쿠키에 회원 정보를 담은 JWT가 포함되어야 한다.

**요청**

- HTTP Method: POST
- Content-Type: application/json
- Request Body:
```json
{
  "reservation": {
    "date": "2025-06-07",
    "timeId": 101,
    "themeId": 1
  },
  "paymentConfirm": {
    "paymentKey": "pay_abc123xyz",
    "orderId": "ORD123456789",
    "amount": 32000
  }
}
```

**응답**

- HTTP Status: 200 OK
- Content-Type: application/json
- 응답 Body:
```json
{
  "id": 7001,
  "memberInfo": {
    "id": 100,
    "name": "홍길동",
    "email": "gildong@example.com",
    "role": "USER"
  },
  "date": "2025-06-07",
  "time": {
    "id": 101,
    "startAt": "10:00:00"
  },
  "theme": {
    "id": 1,
    "name": "공포의 저택",
    "description": "귀신이 사는 저택을 탈출하라!",
    "thumbnail": "https://example.com/images/theme1.png"
  }
}
```
---
