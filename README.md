# API 명세서

## 어드민 권한 페이지
- description: 어드민(ADMIN) 권한을 가진 사람만 접근 가능한 페이지입니다.
- 
### 어드민 메인 페이지
- http method: GET
- uri: /admin
- header
  - cookie: token={token}
- file path: templates/admin/index.html

### 어드민 예약 페이지
- http method: GET
- uri: /admin/reservation
- header
  - cookie: token={token}
- file path: templates/admin/reservation-new.html

### 어드민 시간 페이지
- http method: GET
- uri: /admin/time
- header
  - cookie: token={token}
- file path: templates/admin/time.html

### 어드민 테마 페이지
- http method: GET
- uri: /admin/theme
- header
  - cookie: token={token}
- file path: templates/admin/theme.html

## 회원 권한 페이지
- description: 회원(ADMIN/GUEST) 권한을 가진 사람만 접근 가능한 페이지입니다.

### 내 예약 페이지
- http method: GET
- uri: /member/reservation
- header
  - cookie: token={token}
- file path: templates/reservation-mine.html

### 예약 페이지
- http method: GET
- uri: /reservation
- header
  - cookie: token={token}
- file path: templates/reservation.html

## 모든 사용자 이용 가능한 페이지
- description: 모든 사용자(ADMIN/GUEST/비회원)가 접근 가능한 페이지입니다.

### 메인 페이지
- http method: GET
- uri: /
- file path: templates/index.html

### 로그인 페이지
- http method: GET
- uri: /login
- file path: templates/login.html

### 회원가입 페이지
- http method: GET
- uri: /signup
- file path: templates/signup.html

## 기능 API 목록

### 권한 없는 페이지 접근 불가
- description: 권한이 없는 페이지에 접근할 경우 다음과 같은 응답이 반환됩니다.
  - 회원이 아닌 사용자: 로그인 페이지, 회원가입 페이지 접근 가능
  - 일반 회원: 어드민 권한 페이지 외 접근 가능
  - 어드민: 모든 페이지 접근 가능
- 권한 없음
  ```
    HTTP/1.1 401

    {
    "message": "인증되지 않은 사용자입니다."
    }
  ```
### 인증되지 않은 사용자 접근 불가
- description: 인증하지 않은 사용자가 접근을 시도할 경우 다음과 같은 응답이 반환됩니다.
- 권한 없음
  ```
    HTTP/1.1 403

    {
    "message": "권한이 없는 접근입니다."
    }
  ```
### 모든 예약 조회 - 어드민
- description: 어드민 권한으로 모든 예약 내역을 조회할 수 있습니다.
- uri: /reservations
- http method: GET
- request
  - header
    - cookie: token={token}
  ```
  GET /reservations HTTP/1.1
  cookie: token={token}
  ```
- response
  ```
  HTTP/1.1 200 
  Content-Type: application/json
  
  [
      {
          "id": 1,
          "date": "2023-01-01",
          "time": {
            "id": 1.
            "startAt": "10:00"
          },
          "theme": {
            "id": 1,
            "name": "레벨2 탈출",
            "description": "우테코 레벨2를 탈출하는 내용입니다.",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
          },
          "member": {
            "id": 1,
            "name": "lini",
            "email": "lini@email.com",
            "role": "GUEST"
          },
          "status": "예약"
      }
  ]
  ```
    
### 모든 예약 대기 조회 - 어드민
- description: 어드민 권한으로 모든 예약 대기 내역을 조회할 수 있습니다.
- http method: GET
- uri: /waitings
- request
  - header
    - cookie: token={token}
  ```
  GET /waitings HTTP/1.1
  cookie: token={token}
  ```
- response
  ```
  HTTP/1.1 200 
  Content-Type: application/json
  
  [
      {
          "id": 1,
          "date": "2023-01-01",
          "time": {
            "id": 1.
            "startAt": "10:00"
          },
          "theme": {
            "id": 1,
            "name": "레벨2 탈출",
            "description": "우테코 레벨2를 탈출하는 내용입니다.",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
          },
          "member": {
            "id": 1,
            "name": "lini",
            "email": "lini@email.com",
            "role": "GUEST"
          },
          "status": "예약대기"
      }
  ]
  ```

### 조건별 예약 조회 - 어드민
- description: 어드민 권한으로 멤버, 테마, 시작 날짜, 종료 날짜 조건을 설정하여 예약 내역을 조회할 수 있습니다.
- http method: GET
- uri: /reservations/search?memberId={memberId}&themeId={themeId}&dateFrom={dateFrom}&dateTo={dateTo}
- request
  - header
    - cookie: token={token}
  - queryString
    - memberId: 필수 아님
    - themeId: 필수 아님
    - dateFrom: 필수 아님
    - dateTo: 필수 아님
  ```
  GET /reservations/search?memberId={memberId}&themeId={themeId}&dateFrom={dateFrom}&dateTo={dateTo} HTTP/1.1
  cookie: token={token}
  ```
- response
  ```
  HTTP/1.1 200 
  Content-Type: application/json
  
  [
      {
          "id": 1,
          "date": "2023-01-01",
          "time": {
            "id": 1.
            "startAt": "10:00"
          },
          "theme": {
            "id": 1,
            "name": "레벨2 탈출",
            "description": "우테코 레벨2를 탈출하는 내용입니다.",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
          },
          "member": {
            "id": 1,
            "name": "lini",
            "email": "lini@email.com",
            "role": "GUEST"
          },
          "status": "예약"
      }
  ]
  ```

### 예약 추가 - 어드민
- description: 어드민 권한으로 결제 없이 예약을 추가할 수 있습니다.
- http method: POST
- uri: /reservations
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - date: 예약 날짜
      - 필수 입력값 
      - 날짜 형식: "yyyy-MM-dd"
    - timeId: 시간 식별자
      - 필수 입력값
    - themeId: 테마 식별자
      - 필수 입력값
    - memberId: 사용자 식별자
      - 필수 입력값
  ```
  POST /admin/reservations HTTP/1.1
  cookie: token={token}
  content-type: application/json
  
  {
      "date": "2023-08-05",
      "timeId": 1,
      "themeId": 1,
      "memberId": 1,
  }
  ```
- response
  - 아래 [예약 추가 - 사용자]와 동일합니다.

### 예약 추가 - 회원
- description: 회원 권한으로 본인의 예약을 결제 후 추가할 수 있습니다.
- http method: POST
- uri: /reservations
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - date: 예약 날짜
      - 필수 입력값
      - 날짜 형식: "yyyy-MM-dd"
    - timeId: 시간 식별자
      - 필수 입력값
    - themeId: 테마 식별자
      - 필수 입력값
    - paymentKey: 결제 키 정보
      - 필수 입력값
    - orderId: 주문 식별자
      - 필수 입력값
    - amount: 주문 금액
      - 필수 입력값
  ```
  POST /reservations HTTP/1.1
  cookie: token={token}
  content-type: application/json

  {
    "date": "2024-03-01",
    "themeId": 1,
    "timeId": 1,
    "paymentKey": "gnocsdo8w921o",
    "orderId": 202406092118,
    "amount": 1000
  }
  ```
- response
  - 추가 성공
    ```
    HTTP/1.1 201 
    Location: /reservations/1
    Content-Type: application/json
  
    {
        "id": 1,
        "date": "2023-08-05",
        "status": "예약",
        "time" : {
            "id": 1.
            "startAt": "10:00"
        },
        "theme": {
            "id": 1,
            "name": "레벨2 탈출",
            "description": "우테코 레벨2를 탈출하는 내용입니다.",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        },
        "member": {
          "id": 1,
          "name": "lini",
          "email": "lini@email.com"
        },
        "status": "예약"
    }
    ```
  - 추가 실패: 중복 예약 불가능 오류
    ```
    HTTP/1.1 400
  
    {
      "message": "선택하신 테마와 일정은 이미 예약이 존재합니다."
    }
    ```

  - 추가 실패 : 일정 오류
    ```
    HTTP/1.1 400 
    Content-Type: application/json
  
    {
      "message": "현재보다 이전으로 일정을 설정할 수 없습니다."
    }
    ```
  - 추가 실패 : 날짜 오류
    ```
    HTTP/1.1 400
  
    {
      "message": "올바르지 않은 날짜입니다."
    }
    ```
  - 추가 실패 : 존재하지 않는 시간 오류
    ```
    HTTP/1.1 400
    Content-Type: application/json

    {
    "message": "더이상 존재하지 않는 시간입니다."
    }
    ```
  - 추가 실패 : 존재하지 않는 테마 오류
    ```
    HTTP/1.1 400

    {
    "message": "더이상 존재하지 않는 테마입니다."
    }
    ```
  - 추가 실패 : 이미 예약 혹은 예약 대기가 존재 오류
    ```
    HTTP/1.1 400

    {
    "message": "이미 예약(대기)가 존재하여 예약이 불가능합니다."
    }
    ```

### 예약 대기 추가 - 회원
- description: 회원 권한으로 본인의 예약 대기를 추가합니다.
- http method: POST
- uri: /waitings
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - date: 예약 날짜
      - 필수 입력값
      - 날짜 형식: "yyyy-MM-dd"
    - timeId: 시간 식별자
      - 필수 입력값
    - themeId: 테마 식별자
      - 필수 입력값
  ```
  POST /reservations HTTP/1.1
  content-type: application/json
  cookie: token={token}

  {
    "date": "2024-03-01",
    "themeId": 1,
    "timeId": 1
  }
  ```
- response
  - 추가 성공
    ```
    HTTP/1.1 201 
    Location: /reservations/1
    Content-Type: application/json
  
    {
        "id": 1,
        "date": "2023-08-05",
        "status": "예약",
        "time" : {
            "id": 1.
            "startAt": "10:00"
        },
        "theme": {
            "id": 1,
            "name": "레벨2 탈출",
            "description": "우테코 레벨2를 탈출하는 내용입니다.",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        },
        "member": {
          "id": 1,
          "name": "lini",
          "email": "lini@email.com"
        },
        "status": "예약 대기"
    }
    ```
  - 추가 실패 : 이미 예약 혹은 예약 대기가 존재 오류
    ```
    HTTP/1.1 400

    {
    "message": "이미 예약(대기) 상태입니다."
    }
    ```
  - 추가 실패 : 예약이 없는데, 예약 대기 시도 오류
    ```
    HTTP/1.1 400

    {
    "message": "예약이 가능합니다. 예약으로 다시 시도해주세요."
    }
    ```
    
### 예약 결제 - 회원
- description: 예약 대기에서 결제 대기로 전환된 본인의 예약 내역 내에서 결제 요청을 할 수 있습니다.
- http method: POST
- uri: /reservations/{id}/payment
  - path variable
    - id: 예약 정보 식별자
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - paymentKey: 결제 키 정보
      - 필수 입력값
    - orderId: 주문 식별자
      - 필수 입력값
    - amount: 주문 금액
      - 필수 입력값
  ```
  POST /reservations/{id}/payment HTTP/1.1
  cookie: token={token}
  Content-Type: application/json
  
  {
    "paymentKey": "tgon_234567890",
    "amount": 1000,
    "orderId": "gwdskjfl"
  }
  ```

- response
  - 성공
  ```
  HTTP/1.1 200 OK
  Content-Type: application/json
  
  {
    "reservationId": 1,
    "theme": "테마1",
    "date": "2024-03-01",
    "time": "10:00",
    "status": "예약",
    "payment": {
      "paymentKey": "tgon_234567890"
      "amount": 1000
    }
  }
  ```
  - 실패: 결제 대기 상태가 아님
    ```
    HTTP/1.1 400

    {
      "message": "결재 대기 상태에서만 결재 가능합니다."
    }
    ```
  - 삭제 실패: 일반 사용자가 본인 결제 대기 외의 것을 결제 시도
    ```
    HTTP/1.1 403

    {
      "message": "본인의 예약만 결제할 수 있습니다."
    }
    ```
  - 삭제 실패: 존재하지 않는 결제 대기를 삭제하려고 시도
    ```
    HTTP/1.1 400

    {
      "message": "더이상 존재하지 않는 결제 대기 정보입니다."
    }
    ```

### 예약 삭제 - 어드민
- description: 어드민 권한으로 예약을 삭제할 수 있습니다. 예약이 취소될 때 결제도 함께 취소됩니다.
- http method: DELETE
- cookie: token={token}
- uri: /admin/reservations/{id}
- request
  - header
    - cookie: token={token}
  - path variable
    - id: 예약 정보 식별자
  ```
  DELETE /admin/reservations/{id} HTTP/1.1
  cookie: token={token}
  ```
- response
  - 삭제 성공
    ```
    HTTP/1.1 204
    ```
  - 삭제 실패: 일정이 지난 예약을 삭제 시도
    ```
    HTTP/1.1 400

    {
    "message": "이미 지난 예약은 삭제할 수 없습니다."
    }
    ```
  - 삭제 실패: 관리자 외 예약 삭제 시도
    ```
    HTTP/1.1 403

    {
    "message": "예약 대기를 삭제할 권한이 없습니다."
    }
    ```
    
### 예약 대기 삭제
- description: 회원은 본인의 예약 대기를 삭제할 수 있습니다.
- http method: DELETE
- cookie: token={token}
- uri: /waitings/{id}
- request
  - header
    - cookie: token={token}
  - path variable
    - id: 예약 정보 식별자
  ```
  DELETE /waitings/{id} HTTP/1.1
  cookie: token={token}
  ```
- response
  - 존재하는 id로 삭제 요청
    ```
    HTTP/1.1 204
    ```
  - 삭제 실패: 예약으로 전환된 예약대기/결제대기를 삭제할 수 없다.
    ```
    HTTP/1.1 400

    {
    "message": "예약은 삭제할 수 없습니다. 관리자에게 문의해주세요."
    }
    ```
  - 삭제 실패: 일반 사용자가 본인 예약대기/결제대기 외의 것을 삭제 시도
    ```
    HTTP/1.1 403

    {
    "message": "예약 대기를 삭제할 권한이 없습니다."
    }
    ```
    
### 시간 추가 - 어드민
- description: 어드민 권한으로 시간ㅇ르 삭제할 수 있습니다.
- http method: POST
- uri: /times
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - startAt: 시간
      - 시간 형식: "HH:mm"
      - 필수 입력값
  ```
  POST /times HTTP/1.1
  cookie: token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6ImFkbWluIiwicm9sZSI6IkFETUlOIn0.cwnHsltFeEtOzMHs2Q5-ItawgvBZ140OyWecppNlLoI
  Content-Type: application/json
   
  {
      "startAt": "10:00"
  }
  ```
- response
  - 추가 성공
    ```
    HTTP/1.1 201 
    Location: /times/1
    Content-Type: application/json
  
    {
        "id": 1,
        "startAt": "10:00"
    }
    ```
  - 추가 실패 : 시간 오류
    ```
    HTTP/1.1 400
  
    {
      "message": "올바르지 않은 시간입니다."
    }
    ```
  - 추가 실패: 중복 시간 오류
    ```
    HTTP/1.1 400
  
    {
      "message": "이미 같은 시간이 존재합니다."
    }
    ```  
  - 추가 실패: 관리자 외 추가 시도 오류
    ```
    HTTP/1.1 403

    {
    "message": "권한이 없습니다. 관리자에게 문의해주세요."
    }
    ```

### 시간 조회
- description: 존재하는 시간 목록을 조회합니다.
- http method: GET
- uri: /times
- request
  - header
    - cookie: token={token}
  ```
  GET /times HTTP/1.1
  cookie: token={token}
  ```
- response
   ```
  HTTP/1.1 200 
  Content-Type: application/json
  [
   {
        "id": 1,
        "startAt": "10:00"
    }
  ]
  ```

### 예약 가능한 시간 조회
- description: 존재하는 시간 목록을 예약 가능 여부와 함께 조회합니다.
- http method: GET
- uri: /times/available?date={date}&themeId={themeId}
- request
  - header
    - cookie: token={token}
  - path variable
    - date: 날짜
      - 날짜 형식: "yyyy-MM-dd"
      - 필수 입력값
    - themeId: 테마 식별자
      - 필수 입력값
  ```
  GET /times/available?date={date}&themeId={themeId} HTTP/1.1
  cookie: token={token}
  ```
- response
   ```
  HTTP/1.1 200 
  Content-Type: application/json
  [
   {
        "id": 1,
        "startAt": "10:00"
        "isBooked": true
    },
   {
        "id": 2,
        "startAt": "11:00"
        "isBooked": false
    }
  ]
  ```

### 시간 삭제 - 어드민
- description: 어드민의 권한으로 시간을 삭제합니다.
- http method: DELETE
- cookie: token = {token}
- uri: /times/{id}
- request
  - header
    - cookie: token={token}
  - path variable
    - id: 시간 정보 식별자
  ```
  DELETE /times/{id} HTTP/1.1
  cookie: token={token}
  ```
- response
  - 성공
    ```
    HTTP/1.1 204
    ```
  - 삭제 실패: 이미 예약이 존재하는 시간 삭제 시도 오류
    ```
    HTTP/1.1 400

    {
      "message": "해당 시간에 예약(대기)이 존재해서 삭제할 수 없습니다."
    }
    ```  
  - 삭제 실패: 관리자 외 삭제 시도 오류
    ```
    HTTP/1.1 403

    {
      "message": "권한이 없습니다. 관리자에게 문의해주세요."
    }
    ```

### 테마 추가 - 어드민
- description: 어드민의 권한으로 테마를 추가합니다.
- http method: POST
- uri: /admin/themes
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - name: 테마 이름
      - 형식: 1자 이상, 20자 이하의 중복되지 않는 이름
      - 필수 입력값
    - description: 테마 설명
      - 형식: 100자 이내
      - 필수 입력값
    - thumbnail: 썸네일 주소
      - 형식: 이미지 링크
      - 필수 입력값
  ```
  POST /admin/themes HTTP/1.1
  cookie: token={token}
  content-type: application/json
  
  {
      "name": "레벨2 탈출",
      "description": "우테코 레벨2를 탈출하는 내용입니다.",
      "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
  }
  ```
- response
  - 추가 성공
    ```
    HTTP/1.1 201
    Location: /admin/themes/1
    Content-Type: application/json

    {
    "id": 1,
    "name": "레벨2 탈출",
    "description": "우테코 레벨2를 탈출하는 내용입니다.",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    }
    ```
  - 추가 실패 : 이름 길이 오류
    ```
    HTTP/1.1 400 
    Content-Type: application/json
  
    {
      "message": "이름은 1자 이상, 20자 이하여야 합니다."
    }  
    ```
  - 추가 실패: 중복 이름 오류
    ```
    HTTP/1.1 400

    {
      "message": "이미 존재하는 테마 이름입니다."
    }
    ```
  - 추가 실패: 썸네일 형식 오류
    ```
    HTTP/1.1 400

    {
      "message": "올바르지 않은 썸네일 형식입니다."
    }
    ```
  - 추가 실패: 설명 길이 오류
    ```
    HTTP/1.1 400

    {
      "message": "설명은 100자를 초과할 수 없습니다."
    }
    ```  
  - 추가 실패: 관리자 외 추가 시도 오류
    ```
    HTTP/1.1 403

    {
      "message": "권한이 없습니다. 관리자에게 문의해주세요."
    }
    ```

### 테마 조회
- description: 모든 테마 목록을 조회할 수 있습니다.
- http method: GET
- uri: /themes
- request
  - header
    - cookie: token={token}
  ```
  GET /themes HTTP/1.1
  cookie: token={token}
  ```
- response
   ```
  HTTP/1.1 200 
  Content-Type: application/json
  [
    {
    "id": 1,
    "name": "레벨2 탈출",
    "description": "우테코 레벨2를 탈출하는 내용입니다.",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    }
  ]
  ```
  
### 인기 테마 조회
- description: 최근 일주일 기준 예약이 많은 테마 10개를 조회합니다. (today: 4/8 -> 조회 기간: 4/1~4/7)
- http method: GET
- uri: /themes/popular
- request
  ```
  GET /themes/popular HTTP/1.1
  ```
- response
   ```
  HTTP/1.1 200 
  Content-Type: application/json
  [
    {
    "id": 1,
    "name": "레벨2 탈출",
    "description": "우테코 레벨2를 탈출하는 내용입니다.",
    "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
    }
  ]
  ```

### 테마 삭제 - 어드민
- description: 어드민 권한으로 테마를 삭제합니다.
- http method: DELETE
- uri: /admin/themes/{id}
- request
  - header
    - cookie: token={token}
  - path variable
    - id: 테마 정보 식별자
  ```
  DELETE /themes/{id} HTTP/1.1
  cookie: token={token}
  ```
- response
  - 성공: 존재하는 id로 삭제 요청
    ```
    HTTP/1.1 204
    ```
  - 삭제 실패: 이미 예약이 존재하는 테마 삭제 시도 오류
    ```
    HTTP/1.1 400

    {
      "message": "해당 테마로 예약(대기)이 존재해서 삭제할 수 없습니다."
    }
    ```
  - 삭제 실패: 관리자 외 삭제 시도 오류
    ```
    HTTP/1.1 403

    {
      "message": "권한이 없습니다. 관리자에게 문의해주세요."
    }
    ```
      
### 사용자 회원가입
- description: 회원가입을 진행합니다.
- http method: POST
- uri: /members
- request
  - header
    - content-type: application/json
  - body
    - name: 이름
      - 형식: 1자 이상, 20자 이하
      - 필수 입력값
    - email: 이메일
      - 형식: email 형식
      - 중복 불가
      - 필수 입력값
    - password: 비밀번호
      - 형식: 6자 이상, 12자 이하
      - 필수 입력값
  ```
  POST /members HTTP/1.1
  content-type: application/json
  
  {
    "name": "사용자이름",
    "email": "admin@email.com",
    "password": "lini123"
  } 
  ```
- response
  - 성공 
  ```
  HTTP/1.1 201 OK
  Content-Type: application/json'
  location: /members/1
  ```
  - 회원가입 실패 - 중복된 이메일 오류
    ```
    HTTP/1.1 400

    {
      "message": "이미 가입된 이메일입니다."
    }
    ``` 
  - 추가 실패 : 이름 길이 오류
    ```
    HTTP/1.1 400 
    Content-Type: application/json
  
    {
      "message": "이름은 1자 이상, 20자 이하여야 합니다."
    }  
    ```
  - 추가 실패 : 이메일 형식 오류
    ```
    HTTP/1.1 400 
    Content-Type: application/json
  
    {
    "message": "유효하지 않은 이메일입니다."
    }  
    ```
  - 추가 실패 : 비밀번호 약식 오류
    ```
    HTTP/1.1 400 
    Content-Type: application/json
  
    {
    "message": "비밀번호는 6자 이상 12자 이하여야 합니다."
    }  
    ```

### 사용자 로그인
- description: 비밀번호와 이메일로 로그인을 합니다.
- http method: POST
- uri: /login
- request
  - header
    - cookie: token={token}
    - content-type: application/json
  - body
    - password: 비밀번호
      - 필수 입력값
    - email: 이메일
      - 필수 입력값
  ```
  POST /login HTTP/1.1
  content-type: application/json
  host: localhost:8080
  
  {
    "password": "비밀번호",
    "email": "admin@email.com",
  } 
  ```
- response
  - 로그인 성공 
  ```
  HTTP/1.1 200 OK
  Content-Type: application/json
  Keep-Alive: timeout=60
  Set-Cookie: token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6ImFkbWluIiwicm9sZSI6IkFETUlOIn0.cwnHsltFeEtOzMHs2Q5-ItawgvBZ140OyWecppNlLoI; Path=/; HttpOnly
  ```
  - 로그인 실패: 비밀번호 미입력 오류
    ```
    HTTP/1.1 400

    {
      "message": "비밀번호를 입력해주세요."
    }
    ```  
  - 로그인 실패: 이메일 미입력 오류
    ```
    HTTP/1.1 400

    {
      "message": "이메일을 입력해주세요."
    }
    ```
  - 로그인 실패: 이메일 또는 비밀번호 오류
    ```
    HTTP/1.1 401

    {
      "message": "이메일 또는 비밀번호가 잘못되었습니다."
    }
    ```

### 사용자 로그아웃
- description: 로그아웃 합니다.
- http method: POST
- uri: /logout
  ```
  POST /logout HTTP/1.1
  ```
- response
  - 로그아웃 성공
    ```
    POST /login HTTP/1.1
    content-type: application/json
    Keep-Alive: timeout=60
    Set-Cookie: 

    ```

### 인증 정보 조회
- description: 현재 로그인된 사용자의 인증 정보를 조회합니다.
- http method: GET
- uri: /login/check
- request
  - header
    - cookie: token={token}
  ```
  GET /login/check HTTP/1.1
  cookie: _ga=GA1.1.48222725.1666268105; _ga_QD3BVX7MKT=GS1.1.1687746261.15.1.1687747186.0.0.0; Idea-25a74f9c=3cbc3411-daca-48c1-8201-51bdcdd93164; token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6IuyWtOuTnOuvvCIsInJvbGUiOiJBRE1JTiJ9.vcK93ONRQYPFCxT5KleSM6b7cl1FE-neSLKaFyslsZM
  ```
- response
  ```
  HTTP/1.1 200 OK
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 03 Mar 2024 19:16:56 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked
  
  {
     "name": "어드민"
  }
  ```

### 사용자 조회 - 어드민
- description: 어드민 권한으로 모든 사용자 목록을 조회합니다.
- http method: GET
- uri: /members
- request
  - header
    - cookie: token={token}
  ```
  GET /members HTTP/1.1
  cookie: token={token}
  ```
- response
  ```
  HTTP/1.1 200 
  Content-Type: application/json
  [
    {
      "id": 1,
      "name": "lini",
      "email": "lini@email.com",
      "role": "GUEST"
    }
  ]
  ```
  - 어드민 외에는 권한 없음
    ```
      HTTP/1.1 403

      {
      "message": "권한이 없는 접근입니다."
      }
    ```

### 본인의 예약/예약대기 조회
- description: 로그인된 사용자의 모든 예약/예약 대기/결제 대기 내역을 조회합니다.
- http method: GET
- uri: /members/reservations
- request
  - header
    - cookie: token={token}
  ```
  GET /members/reservations HTTP/1.1
  cookie: token={token}
  ```
- response
```
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "reservationId": 1,
    "theme": "테마1",
    "date": "2024-03-01",
    "time": "10:00",
    "reservationStatus": {
      "status": "예약",
      "rank": 0
    },
    "payment": {
      "paymentKey": "tgon_234567890"
      "amount": 1000
    }
  },
  {
    "reservationId": 2,
    "theme": "테마2",
    "date": "2024-03-01",
    "time": "12:00",
    "reservationStatus": {
      "status": "예약대기",
      "rank": 2
    },
    "payment": {
      "paymentKey": ""
      "amount": 1000
    }
  },
  {
    "reservationId": 3,
    "theme": "테마3",
    "date": "2024-03-01",
    "time": "14:00",
    "reservationStatus": {
      "status": "예약대기",
      "rank": 3
    },
    "payment": {
      "paymentKey": ""
      "amount": 1000
    }
  }
]
```
