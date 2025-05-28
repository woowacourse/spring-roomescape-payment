package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.dto.response.PaymentSuccessResponse;
import roomescape.service.payment.MyClientHttpRequestFactory;
import roomescape.service.payment.PaymentApproveClient;
import roomescape.service.payment.PaymentApproveErrorHandler;

class PaymentApproveClientTest {

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultStatusHandler(new PaymentApproveErrorHandler());

    private final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(testBuilder).build();

    private final PaymentApproveClient paymentApproveClient = new PaymentApproveClient(
            new MyClientHttpRequestFactory(),
            testBuilder,
            "https://api.tosspayments.com",
            "1234"
    );

    @Test
    @DisplayName("외부 API를 통하여 결제 승인을 요청한다.")
    void approveTest() {
        // given
        final String successResponse = successResponse();
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess(successResponse, MediaType.APPLICATION_JSON));

        // when
        final PaymentSuccessResponse response = paymentApproveClient.approvePayment(
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "a4CWyWY5m89PNh7xJwhk1",
                1000
        );

        // then
        assertThat(response.paymentKey()).isEqualTo("5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1");
        mockServer.verify();
    }

    private String successResponse() {
        return """
                {
                  "mId": "tosspayments",
                  "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
                  "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
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
    }

}