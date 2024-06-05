# 방탈출 사용자 예약 관련 API 문서

# 테마 목록 조회 API

현재 운영중인 모든 방탈출 테마 목록을 조회합니다.

## Request
### URL
> http://{host address}/themes
### Method
> GET
### Cookie
> token = {엑세스 토큰}
## Success Response
### Status
> 200 OK
### Body
- id : 테마의 식별자.
- name : 테마의 이름.
- description: 테마의 설명.
- thumbnail: 테마의 썸네일 URL. http:// 혹은 https:// 로 시작하는 URL 이다.
```json
[
  {
    "id": 1,
    "name": "테마 이름",
    "description": "테마 설명",
    "thumbnail": "테마 썸네일 URL"
  },
  {
    "id": 2,
    "name": "테마 이름",
    "description": "테마 설명",
    "thumbnail": "테마 썸네일 URL"
  }
]
```

# 예약 시간 목록 조회 API
특정 날짜와 테마에 예약가능한 시간 목록과 그 시간에 예약이 되어있는지 여부를 조회합니다.

## Request
### URL
> http://{host address}/times
### Query Parameters
- date : 예약하고자 하는 날짜. yyyy-MM-dd 포맷의 문자열이다.
- themeId : 예약하고자 하는 테마의 식별자.
### Method
> GET
### Cookie
> token = {엑세스 토큰}
## Success Response
### Status
> 200 OK
### Body
- id : 시간의 식별자.
- startAt : 예약의 시작 시간. hh-mm 포맷의 문자열이다. 중복된 값은 존재하지 않는다.
- isBooked : 그 시간에 예약이 되어있는지 여부. true 이면 예약이 있음을, false 이면 예약이 없음을 뜻한다.
```json
[
  {
    "id": 1,
    "startAt": "15:00",
    "isBooked": false
  },
  {
    "id": 2,
    "startAt": "15:30",
    "isBooked": true
  }
]
```
## 발생할 수 있는 예외
### 테마 아이디가 잘못된 경우
```json
{
  "message": "없는 테마입니다."
}
```

# 예약 생성 API
특정 날짜와 시간에 특정 테마에 예약을 생성합니다. 테마 생성 이후 토스 페이먼츠에 결제 요청을 보내고, 결제 승인 API 를 호출해야 예약이 완료됩니다.

## Request
### URL
> http://{host address}/reservations
### Body
- date : 예약하고자 하는 날짜. yyyy-MM-dd 포맷의 문자열이다.
- timeId : 예약하고자 하는 시간의 식별자.
- themeId : 예약하고자 하는 테마의 식별자.
```json
{
  "date": "2024-06-05",
  "timeId": 1,
  "themeId": 1
}
```
### Method
> POST
### Cookie
> token = {엑세스 토큰}
## Success Response
### Status
> 201 Created
### Body
- id : 예약의 식별자.
- name : 예약한 회원의 이름.
- date: 예약한 날짜. yyyy-MM-dd 포맷의 문자열이다.
- time : 예약 시간 객체. 자세한 응답은 [예약 시간 목록 조회 API](# 예약 시간 목록 조회 API) 참고해주세요.
- theme : 테마 객체. 자세한 응답은 [테마 목록 조회 API](# 테마 목록 조회 API) 참고해주세요.
```json
{
  "id": 1,
  "name": "예약한 회원 이름",
  "date": "2024-06-05",
  "time": {
    "id": 1,
    "startAt": "15:00"
  },
  "theme": {
    "id": 1,
    "name": "테마 이름",
    "description": "테마 설명",
    "thumbnail": "테마 썸네일"
  }
}
```
## 발생할 수 있는 예외
### 시간 아이디가 잘못된 경우
```json
{
  "message": "존재하지 않는 시간입니다."
}
```
### 테마 아이디가 잘못된 경우
```json
{
  "message": "없는 테마입니다."
}
```
### 그 날짜와 시간에 이미 예약이 있는 테마인 경우
```json
{
  "message": "같은 시간에 이미 예약이 존재합니다."
}
```
### 이미 지난 시간에 예약을 시도하는 경우
```json
{
  "message": "이미 지난 시간에 예약할 수 없습니다."
}
```

# 결제 승인 API
특정 예약에 대한 결제를 승인합니다. 결제 승인 실패 시 다시 결제를 시도하거나 결제 승인을 다시 요청할 수 있습니다.

## Request
### URL
> http://{host address}/payment
### Body
- paymentKey : 토스 페이먼츠 API의 paymentKey
- orderId : 토스 페이먼츠 API의 orderId.
- amount : 토스 페이먼츠 API의 amount.
- reservationId : 예약의 식별자.
```json
{
  "paymentKey": "paymentKey_a6a1303977f4",
  "orderId": "MyNameIsNicoRobinMC4yOTgzNTM1OTE2NjQz",
  "amount": 1000,
  "reservationId": 0
}
```
### Method
> POST
### Cookie
> token = {엑세스 토큰}
## Success Response
### Status
> 200 OK
### Body
없음

## 발생할 수 있는 예외
### 예약 아이디가 잘못된 경우
```json
{
  "message": "없는 예약입니다."
}
```
### 결제 한도가 초과되거나 잔액이 부족한 경우
```json
{
  "message": "한도가 초과되거나 잔액이 부족하여 결제에 실패했습니다."
}
```
### 카드사가 결제를 거부한 경우
```json
{
  "message": "카드사에서 결제를 거부했습니다. 카드사에 문의하세요."
}
```
### 결제 비밀번호가 잘못된 경우
```json
{
  "message": "비밀번호가 잘못되었습니다."
}
```
### 일일 결제 한도를 초과한 경우
```json
{
  "message": "일일 한도를 초과하여 결제에 실패했습니다."
}
```
### 정지된 카드라 결제를 실패한 경우
```json
{
  "message": "정지된 카드라 결제에 실패하였습니다."
}
```
### 그 외의 이유로 결제가 실패한 경우
```json
{
  "message": "결제에 실패하였습니다. 고객센터로 문의해주세요."
}
```

# 예약 대기 생성 API
특정 날짜와 시간에 특정 테마에 예약대기를 생성합니다.

## Request
### URL
> http://{host address}/reservations/waiting
### Body
- date : 예약하고자 하는 날짜. yyyy-MM-dd 포맷의 문자열이다.
- timeId : 예약하고자 하는 시간의 식별자.
- themeId : 예약하고자 하는 테마의 식별자.
```json
{
  "date": "2024-06-05",
  "timeId": 1,
  "themeId": 1
}
```
### Method
> POST
### Cookie
> token = {엑세스 토큰}
## Success Response
### Status
> 200 OK
### Body
- id : 예약 대기의 식별자.
- name : 예약대기한 회원의 이름.
- date: 예약 대기한 날짜. yyyy-MM-dd 포맷의 문자열이다.
- time : 예약 시간 객체. 자세한 응답은 [예약 시간 목록 조회 API](# 예약 시간 목록 조회 API) 참고해주세요.
- theme : 테마 객체. 자세한 응답은 [테마 목록 조회 API](# 테마 목록 조회 API) 참고해주세요.
- priority : 예약 우선 순위. 1 부터 시작하며, 현재 예약을 포함해 자신보다 앞 순위의 대기자 수를 의미한다.
```json
{
  "id": 1,
  "name": "예약한 회원 이름",
  "date": "2024-06-05",
  "time": {
    "id": 1,
    "startAt": "15:00"
  },
  "theme": {
    "id": 1,
    "name": "테마 이름",
    "description": "테마 설명",
    "thumbnail": "테마 썸네일"
  },
  "priority": 1
}
```
## 발생할 수 있는 예외
### 시간 아이디가 잘못된 경우
```json
{
  "message": "존재하지 않는 시간입니다."
}
```
### 테마 아이디가 잘못된 경우
```json
{
  "message": "없는 테마입니다."
}
```
### 그 날짜와 시간에 아직 예약이 없는 테마인 경우
```json
{
  "message": "예약 대기는 예약이 있어야만 생성할 수 있습니다."
}
```
### 이미 지난 시간에 예약을 시도하는 경우
```json
{
  "message": "이미 지난 시간에 예약할 수 없습니다."
}
```
### 중복된 예약 대기를 시도할 경우
```json
{
  "message": "같은 테마와 날짜, 시간에 예약 대기는 한번만 생성할 수 있습니다."
}
```
