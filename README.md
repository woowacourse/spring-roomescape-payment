# 방탈출 예약 대기

## 개요

방탈출과 예약 내역을 관리하고, 사용자가 방탈출을 예약할 수 있도록 돕는 웹 애플리케이션입니다.

## 제공 기능

방탈출과 예약을 관리하는 `관리자`와 방탈출을 예약하는 `사용자`의 기능을 제공합니다.

[Quick Start](#quick-start) 섹션으로 이동하여 애플리케이션을 실행하고 기능을 체험할 수 있습니다.

## 추가된 화면 및 기능

### 예약 과정에 결제 기능 추가

![reservation_page](images/reservation_page.png)

![reservation_payment](images/reservation_toss_payment.png)

![success_reserve](images/success_reserve.png)

- 예약 과정에서 결제 기능이 추가되었습니다.
- 결제창 UI가 추가되어 사용자가 결제할 수 있도록 합니다.
- 결제 승인 API를 호출하여 결제를 처리합니다.
- 결제 실패 시 에러 핸들링이 구현되어, 결제 실패 사유를 안내합니다.
- 결제 승인 후 예약이 완료되며, 예약 목록에 추가됩니다.

### 결제 전 결제 금액 검증

![pre_request_valid_amount](images/pre_request_valid_amount.png)

- 결제 승인 전 결제 금액을 [검증](https://docs.tosspayments.com/guides/learn/payment-flow#%EA%B2%B0%EC%A0%9C-%EC%A0%95%EB%B3%B4-%EA%B2%80%EC%A6%9D%ED%95%98%EA%B8%B0)하여, 잘못된 금액으로 결제가 이루어지지 않도록 합니다.
    - 요청과 승인 사이에 악의적으로 결제 금액을 수정할 수 있기 때문에 orderId(주문번호)와 amount(최종 결제 금액)을 클라이언트에서 서버로 보내 임시로 저장합니다.

## 요구사항 (변경 사항)

- 결제 기능 구현
    - [x] 결제창 UI 추가
    - [x] 결제 승인 API 호출
        - 에러 핸들링 구현
            - [x] 실패 시 결제 실패 사유 안내
            - [x] 결제 승인 전 결제 금액 검증

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
