package roomescape.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import roomescape.dto.response.PaymentSuccessResponse;
import roomescape.global.exception.payment.PaymentException;
import roomescape.service.payment.PaymentApproveClient;

@SpringBootTest
@EnableWireMock
@TestPropertySource(properties = {
        "toss.payments.base-url=http://localhost:${wiremock.server.port}"
})
class PaymentApproveClientTest {

    private static final String PAYMENT_KEY = "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1";
    private static final String ORDER_ID = "a4CWyWY5m89PNh7xJwhk1";
    private static final int AMOUNT = 1000;
    private static final String CONFIRM_ENDPOINT = "/v1/payments/confirm";

    @Autowired
    private PaymentApproveClient paymentApproveClient;

    @InjectWireMock
    private WireMockServer wireMock;

    @Test
    @DisplayName("외부 API를 통하여 결제 승인을 요청한다.")
    void approvePaymentSuccessfully() {
        // given
        wireMock.stubFor(post(CONFIRM_ENDPOINT)
                .willReturn(okJson(createSuccessResponse())));

        // when
        PaymentSuccessResponse response = paymentApproveClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT);

        // then
        assertThat(response.paymentKey()).isEqualTo(PAYMENT_KEY);
        assertThat(response.totalAmount()).isEqualTo(AMOUNT);
    }

    @Test
    @DisplayName("외부 API를 통해 결제 승인을 요청했지만 에러 반환받음")
    void approvePaymentWithError() {
        // given
        String errorMessage = "존재하지 않는 결제 입니다.";
        wireMock.stubFor(post(CONFIRM_ENDPOINT)
                .willReturn(jsonResponse(createErrorResponse("NOT_FOUND_PAYMENT", errorMessage), 400)));

        // when & then
        assertThatThrownBy(() -> paymentApproveClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining(errorMessage);
    }

    @Test
    @DisplayName("외부 API를 통해 결제 승인을 요청했지만 민감한 에러 반환받음")
    void approvePaymentWithSensitiveError() {
        // given
        wireMock.stubFor(post(CONFIRM_ENDPOINT)
                .willReturn(jsonResponse(createErrorResponse("INVALID_API_KEY", "잘못된 시크릿키 연동 정보 입니다."), 400)));

        // when & then
        assertThatThrownBy(() -> paymentApproveClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining(PaymentException.SENSITIVE_EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("타임아웃 발생 시 RestClientException이 발생한다")
    void approvePaymentWithTimeout() {
        // given
        wireMock.stubFor(post(CONFIRM_ENDPOINT)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
                        .withFixedDelay(30000)));

        // when & then
        assertThatThrownBy(() -> paymentApproveClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT))
                .isInstanceOf(RestClientException.class);
    }

    private String createSuccessResponse() {
        return """
                {
                  "mId": "tosspayments",
                  "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
                  "paymentKey": "%s",
                  "orderId": "%s",
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
                    "amount": %d
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
                    "url": "https://api.tosspayments.com/v1/payments/%s/checkout"
                  },
                  "currency": "KRW",
                  "totalAmount": %d,
                  "balanceAmount": %d,
                  "suppliedAmount": 909,
                  "vat": 91,
                  "taxFreeAmount": 0,
                  "metadata": null,
                  "method": "카드",
                  "version": "2022-11-16"
                }
                """.formatted(PAYMENT_KEY, ORDER_ID, AMOUNT, PAYMENT_KEY, AMOUNT, AMOUNT);
    }

    private String createErrorResponse(String code, String message) {
        return """
                {
                  "code": "%s",
                  "message": "%s"
                }
                """.formatted(code, message);
    }
}
