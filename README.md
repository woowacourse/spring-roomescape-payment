# 방탈출 예약 대기

## 개요

방탈출과 예약 내역을 관리하고, 사용자가 방탈출을 예약할 수 있도록 돕는 웹 애플리케이션입니다.

## 제공 기능

방탈출과 예약을 관리하는 `관리자`와 방탈출을 예약하는 `사용자`의 기능을 제공합니다.

[Quick Start](#quick-start) 섹션으로 이동하여 애플리케이션을 실행하고 기능을 체험할 수 있습니다.

## 요구사항 (변경 사항)

- 결제 기능 구현
    - [x] 결제창 UI 추가
    - [x] 결제 승인 API 호출
        - 에러 핸들링 구현
            - [x] 실패 시 결제 실패 사유 안내

# Quick Start

애플리케이션 실행 후 아래의 링크로 접속할 수 있습니다.

- 관리자 페이지: [localhost:8080/admin](http://localhost:8080/admin)
- 사용자 페이지: [localhost:8080/](http://localhost:8080/)

## 더미 계정

관리자와 사용자의 더미 계정은 아래와 같습니다. 회원가입 후 로그인도 가능합니다.

```text
관리자 계정: admin@email.com
비밀번호: password
사용자 계정: normal@email.com
비밀번호: password
사용자 계정: jihun@email.com
비밀번호: password
-- 더 많은 계정은 data.sql 파일을 참고하세요.
```
