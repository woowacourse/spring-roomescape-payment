
**에러 응답 형식**

모든 에러 응답은 다음과 같은 JSON 구조를 따른다.

```json
// 에러 응답 body 예시
{
  "status": 401,
  "code": "EXPIRED_TOKEN",
  "message": "만료된 토큰입니다.",
  "path": "GET /login/check",
  "timestamp": "2025-06-06T16:38:55.249063"
}
```

**응답 필드 설명**

| 필드명     | 타입        | 설명                                      |
|------------|-------------|-------------------------------------------|
| status     | int         | HTTP 상태 코드                            |
| code       | string      | 에러 식별자 (도메인 내에서 유일한 코드)  |
| message    | string      | 사용자에게 보여줄 에러 메시지             |
| path       | string      | 요청 메서드 및 URI 경로                   |
| timestamp  | string (ISO)| 에러 발생 시각 (서버 기준 ISO-8601)       |

**에러 코드 목록**

결제 승인 및 예약 추가 API(POST /reservations)는 아래에 기재된 코드 이외의 에러 코드가 반환될 수 있다.
해당 에러 코드에 대한 상태 코드와 설명은 해당 [링크](https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8)를 참고한다.

| 코드                          | 상태  | 설명                              |
| --------------------------- | --- | ------------------------------- |
| `BAD_REQUEST`               | 400 | 요청 형식이 잘못되었거나 유효하지 않은 파라미터가 포함됨 |
| `UNAUTHORIZED`              | 401 | 인증 정보가 없거나 유효하지 않아 요청이 거부됨      |
| `FORBIDDEN`                 | 403 | 권한이 없는 리소스에 접근을 시도함             |
| `NOT_FOUND`                 | 404 | 요청한 리소스가 존재하지 않음                |
| `INTERNAL_SERVER_ERROR`     | 500 | 서버 내부에서 처리되지 않은 예외가 발생함         |
| `CONFLICT`                  | 409 | 이미 존재하거나 충돌이 발생한 리소스에 대한 요청     |
| `INVALID_INPUT`             | 400 | 필드 값의 형식이나 제약 조건이 위반됨           |
| `INVALID_AUTH_INFO`         | 401 | 로그인 정보(이메일, 비밀번호 등)가 올바르지 않음    |
| `EXPIRED_TOKEN`             | 401 | 인증 토큰이 만료되어 재인증이 필요함            |
| `INVALID_DATETIME_FORMAT`   | 400 | 날짜나 시간 포맷이 형식에 맞지 않음            |
| `MEMBER_NOT_FOUND`          | 401 | 요청한 사용자가 시스템에 존재하지 않음           |
| `MUST_BE_MEMBER`            | 401 | 인증되지 않은 사용자로 인해 요청이 거부됨         |
| `INVALID_MEMBER_INFO`       | 401 | 이메일 또는 비밀번호가 일치하지 않아 로그인에 실패함   |
| `CONFLICT_RESERVATION_TIME` | 409 | 해당 시간에 예약이 존재하여 삭제할 수 없음        |
