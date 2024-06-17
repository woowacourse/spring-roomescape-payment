package roomescape.payment.client;

import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;

public class SampleTossPaymentConst {


    public static final PaymentRequest paymentRequest = new PaymentRequest(
            "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "MC4wODU4ODQwMzg4NDk0", 1000L, "카드");

    public static final String paymentRequestJson = """
            {
              "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
              "orderId": "MC4wODU4ODQwMzg4NDk0",
              "amount": 1000,
              "paymentType": "카드"
            }
            """;

    public static final PaymentCancelRequest cancelRequest = new PaymentCancelRequest(
            "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", 1000L, "테스트 결제 취소");

    public static final String cancelRequestJson = """
            {
              "cancelReason": "테스트 결제 취소"
            }
            """;

    public static final String tossPaymentErrorJson = """
            {
                "code": "ERROR_CODE",
                "message": "Error message"
            }
            """;

    public static final String confirmJson = """
            {
              "mId": "tosspayments",
              "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
              "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
              "orderId": "MC4wODU4ODQwMzg4NDk0",
              "orderName": "토스 티셔츠 외 2건",
              "taxExemptionAmount": 0,
              "status": "DONE",
              "requestedAt": "2024-02-13T12:17:57+09:00",
              "approvedAt": "2024-02-13T12:18:14+09:00",
              "useEscrow": false,
              "cultureExpense": false,
              "card": {
                "issuerCode": "71",
                "acquirerCode": "71",
                "number": "12345678****000*",
                "installmentPlanMonths": 0,
                "isInterestFree": false,
                "interestPayer": null,
                "approveNo": "00000000",
                "useCardPoint": false,
                "cardType": "신용",
                "ownerType": "개인",
                "acquireStatus": "READY",
                "receiptUrl": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX",
                "amount": 1000
              },
              "virtualAccount": null,
              "transfer": null,
              "mobilePhone": null,
              "giftCertificate": null,
              "cashReceipt": null,
              "cashReceipts": null,
              "discount": null,
              "cancels": null,
              "secret": null,
              "type": "NORMAL",
              "easyPay": {
                "provider": "토스페이",
                "amount": 0,
                "discountAmount": 0
              },
              "easyPayAmount": 0,
              "easyPayDiscountAmount": 0,
              "country": "KR",
              "failure": null,
              "isPartialCancelable": true,
              "receipt": {
                "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX"
              },
              "checkout": {
                "url": "https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/checkout"
              },
              "currency": "KRW",
              "totalAmount": 1000,
              "balanceAmount": 1000,
              "suppliedAmount": 909,
              "vat": 91,
              "taxFreeAmount": 0,
              "method": "카드",
              "version": "2022-11-16"
            }
            """;

    public static final String cancelJson = """
            {
              "mId": "tosspayments",
              "lastTransactionKey": "090A796806E726BBB929F4A2CA7DB9A7",
              "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
              "orderId": "MC4wODU4ODQwMzg4NDk0",
              "orderName": "토스 티셔츠 외 2건",
              "taxExemptionAmount": 0,
              "status": "CANCELED",
              "requestedAt": "2024-02-13T12:17:57+09:00",
              "approvedAt": "2024-02-13T12:18:14+09:00",
              "useEscrow": false,
              "cultureExpense": false,
              "card": {
                "issuerCode": "71",
                "acquirerCode": "71",
                "number": "12345678****000*",
                "installmentPlanMonths": 0,
                "isInterestFree": false,
                "interestPayer": null,
                "approveNo": "00000000",
                "useCardPoint": false,
                "cardType": "신용",
                "ownerType": "개인",
                "acquireStatus": "READY",
                "amount": 1000
              },
              "virtualAccount": null,
              "transfer": null,
              "mobilePhone": null,
              "giftCertificate": null,
              "cashReceipt": null,
              "cashReceipts": null,
              "discount": null,
              "cancels": [
                {
                  "transactionKey": "090A796806E726BBB929F4A2CA7DB9A7",
                  "cancelReason": "테스트 결제 취소",
                  "taxExemptionAmount": 0,
                  "canceledAt": "2024-02-13T12:20:23+09:00",
                  "easyPayDiscountAmount": 0,
                  "receiptKey": null,
                  "cancelAmount": 1000,
                  "taxFreeAmount": 0,
                  "refundableAmount": 0,
                  "cancelStatus": "DONE",
                  "cancelRequestId": null
                }
              ],
              "secret": null,
              "type": "NORMAL",
              "easyPay": {
                "provider": "토스페이",
                "amount": 0,
                "discountAmount": 0
              },
              "easyPayAmount": 0,
              "easyPayDiscountAmount": 0,
              "country": "KR",
              "failure": null,
              "isPartialCancelable": true,
              "receipt": {
                "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX"
              },
              "checkout": {
                "url": "https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/checkout"
              },
              "currency": "KRW",
              "totalAmount": 1000,
              "balanceAmount": 0,
              "suppliedAmount": 0,
              "vat": 0,
              "taxFreeAmount": 0,
              "method": "카드",
              "version": "2022-11-16"
            }
            """;
}
