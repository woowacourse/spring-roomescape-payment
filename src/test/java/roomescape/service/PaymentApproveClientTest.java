package roomescape.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import roomescape.dto.response.PaymentSuccessResponse;
import roomescape.service.payment.PaymentApproveClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@EnableWireMock
@TestPropertySource(properties = {
        "toss.payments.base-url=http://localhost:${wiremock.server.port}"
})
class PaymentApproveClientTest {

    @Autowired
    private PaymentApproveClient paymentApproveClient;

    @InjectWireMock
    WireMockServer wireMock;

    @Test
    @DisplayName("외부 API를 통하여 결제 승인을 요청한다.")
    void approveTest() {
        // given
        wireMock.stubFor(post("/v1/payments/confirm")
                .willReturn(okJson(successResponse())));

        // when
        final PaymentSuccessResponse response = paymentApproveClient.approvePayment(
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "a4CWyWY5m89PNh7xJwhk1",
                1000
        );

        // then
        assertThat(response.paymentKey()).isEqualTo("5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1");
    }

    @Test
    @DisplayName("외부 API를 통해 결제 승인을 요청했지만 에러 반환받음")
    void approveFailureTest() {
        // given
        String errorCode = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;
        wireMock.stubFor(post("/v1/payments/confirm")
                .willReturn(jsonResponse(errorCode, 400)));

        // when, then
        assertThatThrownBy(() -> paymentApproveClient.approvePayment(
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "a4CWyWY5m89PNh7xJwhk1",
                1000
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("존재하지 않는 결제 입니다.");
    }

    @Test
    @DisplayName("외부 API를 통해 결제 승인을 요청했지만 민감한 에러 반환받음")
    void approveSensitiveFailureTest() {
        // given
        String errorCode = """
                {
                  "code": "INVALID_API_KEY",
                  "message": "잘못된 시크릿키 연동 정보 입니다."
                }
                """;
        wireMock.stubFor(post("/v1/payments/confirm")
                .willReturn(jsonResponse(errorCode, 400)));

        // when, then
        assertThatThrownBy(() -> paymentApproveClient.approvePayment(
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "a4CWyWY5m89PNh7xJwhk1",
                1000
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 승인 중 예외가 발생하였습니다.");
    }

    @Test
    @DisplayName("외부 API를 통해 결제 승인을 요청했지만 타임아웃 됨")
    void approveFailureTimeoutTest() {
        // given
        wireMock.stubFor(post("/v1/payments/confirm")
                .willReturn(aResponse().withFixedDelay(10000)));

        // when, then
        assertThatThrownBy(() -> paymentApproveClient.approvePayment(
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "a4CWyWY5m89PNh7xJwhk1",
                1000
        )).isInstanceOf(RestClientException.class);
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
