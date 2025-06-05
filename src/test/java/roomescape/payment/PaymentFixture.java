package roomescape.payment;

public class PaymentFixture {

    public static final String CONFIRM_SUCCESS_RESPONSE_BODY =
                        """

                                {
                                  "mId": "tosspayments",
                                  "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
                                  "paymentKey": "test_key",
                                  "orderId": "a4CWyWY5m89PNh7xJwhk1",
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
                                  "metadata": null,
                                  "method": "카드",
                                  "version": "2022-11-16"
                                }
                        """;

    public static final String ERROR_RESPONSE_BODY =
            """
              {
                "code": "INVALID_API_KEY",
                "message": "잘못된 시크릿키 연동 정보 입니다."
              }
            """;

    public static final String CANCEL_SUCCESS_RESPONSE_BODY = """
            {
              "mId": "tgen_docs",
              "lastTransactionKey": "txrd_a01jx0he4vdah35652ans06bnm9",
              "paymentKey": "tgen_20250606021349bXZN7",
              "orderId": "MC43MDg3ODI2MTI3OTAx",
              "orderName": "토스 티셔츠 외 2건",
              "taxExemptionAmount": 0,
              "status": "CANCELED",
              "requestedAt": "2025-06-06T02:13:49+09:00",
              "approvedAt": "2025-06-06T02:14:25+09:00",
              "useEscrow": false,
              "cultureExpense": false,
              "card": null,
              "virtualAccount": null,
              "transfer": null,
              "mobilePhone": null,
              "giftCertificate": null,
              "cashReceipt": null,
              "cashReceipts": null,
              "discount": null,
              "cancels": [
                {
                  "transactionKey": "txrd_a01jx0he4vdah35652ans06bnm9",
                  "cancelReason": "구매자 변심",
                  "taxExemptionAmount": 0,
                  "canceledAt": "2025-06-06T02:15:19+09:00",
                  "transferDiscountAmount": 0,
                  "easyPayDiscountAmount": 24,
                  "receiptKey": null,
                  "cancelStatus": "DONE",
                  "cancelRequestId": null,
                  "cancelAmount": 50000,
                  "taxFreeAmount": 0,
                  "refundableAmount": 0
                }
              ],
              "secret": "ps_yL0qZ4G1VOQDAGbm5oO8oWb2MQYg",
              "type": "NORMAL",
              "easyPay": {
                "provider": "토스페이",
                "amount": 49976,
                "discountAmount": 24
              },
              "country": "KR",
              "failure": null,
              "isPartialCancelable": true,
              "receipt": {
                "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tgen_20250606021349bXZN7&ref=PX"
              },
              "checkout": {
                "url": "https://api.tosspayments.com/v1/payments/tgen_20250606021349bXZN7/checkout"
              },
              "currency": "KRW",
              "totalAmount": 50000,
              "balanceAmount": 0,
              "suppliedAmount": 0,
              "vat": 0,
              "taxFreeAmount": 0,
              "method": "간편결제",
              "version": "2022-11-16",
              "metadata": null
            }
            """;
}
