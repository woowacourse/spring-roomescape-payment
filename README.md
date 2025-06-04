# 방탈출 예약 결제/배포

## 접근 방법

애플리케이션 실행 후 아래의 링크로 접속할 수 있습니다.

- 관리자 페이지: [localhost:8080/admin](http://localhost:8080/admin)
    ```plaintext
      테스트 관리자 계정
      email: danny@example.com
      password: 0000
    ```

- 사용자 페이지: [localhost:8080/](http://localhost:8080/)
    ```plaintext
  테스트 사용자 계정
  email: sooyang@example.com
  password: 1234
   ```

## 기능 구현 목록

- [x] 화면 수정 - 결제 UI
- [x] 기능 수정
    - [x] 사용자가 결제를 완료해야 예약할 수 있도록 수정
    - [x] 외부 결제 API 연동
        - [x] 사용자에게 결제 실패 사유 제공
