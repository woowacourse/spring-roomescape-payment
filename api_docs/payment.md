# 결제

결제 REST API 엔드포인트에 대한 문서입니다.

실패 응답에 대해서는 [여기](exception.md)를 참고해주세요.

## 목차
- [결제 승인 요청하기](#결제-승인-요청하기)

## 결제 승인 요청하기

> **POST /payments/confirm**

### Request

- **본문**
    
  |      필드       |  자료형   |  예시 값  |      설명      |
  |:-------------:|:------:|:------:|:------------:|
  | reservationId | Number |   1    | 결제하려는 예약의 ID |
  |  paymentKey   | String | "abcd" |   결제 식별 키    |
  |    orderId    | String | "xyz"  |   주문 식별 번호   |
  |    amount     | Number | 10000  |    결제할 금액    |

### Response

> **200 OK**

---
