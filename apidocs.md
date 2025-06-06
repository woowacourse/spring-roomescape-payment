# 방탈출 예약 서비스 API

방탈출 예약 페이지에서 호출하는 API 목록을 정리한 문서입니다.

## API 명세 요약

| **구분** |       **엔드포인트**       | **메서드** |                 **설명**                  |
|:------:|:---------------------:|:-------:|:---------------------------------------:|
|   멤버   |     /login/check      |   GET   |      회원 가입을 한 사용자의 로그인 여부를 조회합니다.       |
|   예약   |     /reservations     |  POST   |  사용자가 선택한 날짜, 테마 및 시간대로 신규 예약을 생성합니다.   |
|        | /reservations/waiting |  POST   | 사용자가 선택한 날짜, 테마 및 시간대로 신규 예약 대기를 생성합니다. |
|   테마   |        /themes        |   GET   |            모든 테마 목록을 조회합니다.             |
| 예약 시간  |   /times/available    |   GET   |    선택한 날짜와 테마 id에 해당하는 예약 시간을 조회합니다.    |

---

## 멤버

### 🟢 GET `/login/check`

회원 가입을 한 사용자의 로그인 여부를 조회합니다.

### 요청
- 요청 헤더 설정
  - Cookie: `token=${JWT_토큰_값}`

### 응답
- 응답 파라미터

    | **필드명** | **타입**  | **설명** |
    |:-------:|:-------:|:------:|
    |   id    | Integer |        |
    |  name   | String  | 사용자 이름 |

- 응답 본문 예제
    ```json
    {
        "id": 1,
        "name": "유저"
    }
    ```

- 상태코드

  | **코드** |         **설명**         |
  |:------:|:----------------------:|
  |  200   |           성공           |
  |  401   | 유효하지 않은 JWT 토큰으로 인한 실패 |

---

## 예약

### 🟠 POST `/reservations`

사용자가 선택한 날짜, 테마 및 시간대로 신규 예약을 생성합니다.

### 요청
- 요청 헤더 설정
  - Cookie: `token=${JWT_토큰_값}`


- 요청 본문 파라미터

  |   **필드명**   | **타입**  |         **설명**         |
  |:-----------:|:-------:|:----------------------:|
  |    date     | String  | 예약 날짜 (형식: yyyy-MM-dd) |
  |   themeId   | Integer |         테마 id          |
  |   timeId    | Integer |        예약 시간 id        |
  | paymentKey  | String  |   외부 결제 API가 발급한 키값    |
  |   orderId   | String  |      랜덤으로 생성한 식별자      |
  |   amount    | Integer |         결제 금액          |
  | paymentType | String  |         결제 유형          |

- 요청 본문 예제
    ```json
    {
        "date": "2025-06-20",
        "themeId": "1",
        "timeId": "1",
        "paymentKey": "tgen_202506051941327v0d7",
        "orderId": "JULIEMC4wMTgwNTMzMTU1MzM3",
        "amount": 1000,
        "paymentType": "NORMAL"
    }
    ```

### 응답
- 응답 파라미터

    | **필드명** | **타입**  |             **설명**             |
    |:-------:|:-------:|:------------------------------:|
    |   id    | Integer |                                |
    |  date   | String  |     예약 날짜 (형식: yyyy-MM-dd)     |
    | member  | Object  |    회원 정보 (필드 구성은 멤버 API 참고)    |
    |  theme  | Object  |    테마 정보 (필드 구성은 테마 API 참고)    |
    |  time   | Object  | 예약 시간 정보 (필드 구성은 예약 시간 API 참고) |

- 응답 본문 예제
    ```json
    {
        "id": 1,
        "date": "2025-06-20",
        "member": {
            "id": 1,
            "name": "유저"
        },
        "theme": {
            "id": 1,
            "name": "theme1",
            "description": "description",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        },
        "time": {
            "id": 1,
            "startAt": "10:00:00"
        }
    }
    ```

- 상태코드

  | **코드** |         **설명**         |
  |:------:|:----------------------:|
  |  201   |           성공           |
  |  400   |   잘못된 요청 파라미터로 인한 실패   |
  |  401   | 유효하지 않은 JWT 토큰으로 인한 실패 |

---

### 🟠 POST `/reservations/waiting`

사용자가 선택한 날짜, 테마 및 시간대로 신규 예약 대기를 생성합니다.

### 요청
- 요청 헤더 설정
  - Cookie: `token=${JWT_토큰_값}`


- 요청 본문 파라미터

  |   **필드명**   | **타입**  |         **설명**         |
  |:-----------:|:-------:|:----------------------:|
  |    date     | String  | 예약 날짜 (형식: yyyy-MM-dd) |
  |   themeId   | Integer |         테마 id          |
  |   timeId    | Integer |        예약 시간 id        |

- 요청 본문 예제

    ```json
    {
        "date": "2025-06-21",
        "themeId": "3",
        "timeId": "3"
    }
    ```

### 응답
- 응답 파라미터

  | **필드명** | **타입**  |             **설명**             |
  |:-------:|:-------:|:------------------------------:|
  |   id    | Integer |                                |
  |  date   | String  |   예약 대기 날짜 (형식: yyyy-MM-dd)    |
  | member  | Object  |    회원 정보 (필드 구성은 멤버 API 참고)    |
  |  theme  | Object  |    테마 정보 (필드 구성은 테마 API 참고)    |
  |  time   | Object  | 예약 시간 정보 (필드 구성은 예약 시간 API 참고) |

- 응답 본문 예제
    ```json
    {
        "id": 4,
        "date": "2025-06-21",
        "member": {
            "id": 1,
            "name": "유저"
        },
        "theme": {
            "id": 3,
            "name": "theme3",
            "description": "description",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        },
        "time": {
            "id": 3,
            "startAt": "10:20:00"
        }
    }
    ```

- 상태코드

  | **코드** |         **설명**         |
  |:------:|:----------------------:|
  |  201   |           성공           |
  |  400   |   잘못된 요청 파라미터로 인한 실패   |
  |  401   | 유효하지 않은 JWT 토큰으로 인한 실패 |

---

## 테마

### 🟢 GET `/themes`

모든 테마 목록을 조회합니다.

### 요청
- 요청 헤더 설정
  - Cookie: `token=${JWT_토큰_값}`

### 응답
- 응답 파라미터

  |   **필드명**   | **타입**  |   **설명**   |
  |:-----------:|:-------:|:----------:|
  |     id      | Integer |            |
  |    name     | String  |   테마 이름    |
  | description | String  | 테마 시나리오 설명 |
  |  thumbnail  | String  | 썸네일 이미지 주소 |

- 응답 본문 예제
    ```json
    [
        {
            "id": 1,
            "name": "theme1",
            "description": "description",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        },
        {
            "id": 2,
            "name": "theme2",
            "description": "description",
            "thumbnail": "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        }
    ]
    ```

- 상태코드

  | **코드** |         **설명**         | 
  |:------:|:----------------------:|
  |  200   |           성공           |
  |  401   | 유효하지 않은 JWT 토큰으로 인한 실패 |

---

## 예약 시간

### 🟢 GET `/times/available`

선택한 날짜와 테마 id에 해당하는 예약 시간을 조회합니다.

### 요청
- 요청 헤더 설정
  - Cookie: `token=${JWT_토큰_값}`


- 요청 쿼리 파라미터

  | **key** | **value**  |   **예제**   |
  |:-------:|:----------:|:----------:|
  |  date   | yyyy-MM-dd | 2025-06-14 |
  | themeId |     n      |     2      |

  - URI 예제: http://43.202.56.128:8080/times/available?date=2025-06-14&themeId=2

### 응답
- 응답 파라미터

  |    **필드명**    | **타입**  |         **설명**         |
  |:-------------:|:-------:|:----------------------:|
  |      id       | Integer |                        |
  |    startAt    | String  |  예약 시간 (형식: HH:mm:ss)  |
  | alreadyBooked | Boolean | 해당 예약 시간에 예약이 존재하는지 여부 |

- 응답 본문 예제
    ```json
    [
        {
            "id": 1,
            "startAt": "10:00:00",
            "alreadyBooked": true
        },
        {
            "id": 2,
            "startAt": "10:10:00",
            "alreadyBooked": false
        }
    ]
    ```

- 상태코드

  | **코드** |         **설명**         |
  |:------:|:----------------------:|
  |  200   |           성공           |
  |  400   |   잘못된 요청 파라미터로 인한 실패   |
  |  401   | 유효하지 않은 JWT 토큰으로 인한 실패 |
