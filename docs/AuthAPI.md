### 회원가입

**POST /members**

Request Body

```json
{
  "email": "test@example.com",
  "password": "password",
  "name": "돔푸"
}
```

Response Body

```json
{
  "id": 1,
  "email": "test@example.com",
  "name": "돔푸"
}
```

### 로그인

**POST /login**

Request Body

```json
{
  "email": "test@example.com",
  "password": "password"
}
```

### 내 정보 조회

**GET /login/check**

Response Body

```json
{
  "name": "돔푸"
}
```

### 로그아웃

**POST /logout**

### 회원 전체 조회 (어드민 기능)

**GET /admin/members**

Response Body

```json
[
  {
    "id": 1,
    "name": "돔푸"
  },
  {
    "id": 2,
    "name": "차니"
  }
]

```
