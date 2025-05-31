package roomescape.payment;

public class PaymentFixture {

    public static final String SUCCESS_RESPONSE_BODY =
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
}
