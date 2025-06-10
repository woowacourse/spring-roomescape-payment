package roomescape.payment.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import fixture.PaymentFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.global.error.exception.ExternalApiClientException;
import roomescape.payment.config.TossPaymentRestClientConfig;
import roomescape.payment.entity.Payment;

@RestClientTest(TossPaymentClient.class)
@ContextConfiguration(classes = TossPaymentRestClientConfig.class)
@Import(TossPaymentClient.class)
class TossPaymentClientTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private TossPaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @Test
    @DisplayName("결제 승인 - 실패 - 결제 승인 금액 조작")
    void requestPaymentConfirm_FailsWithInvalidAmount() {
        // given
        String serverExpectedBody = """
                {
                    "paymentKey": "testPaymentKey",
                    "orderId": "testOrderId",
                    "totalAmount": 99999,
                    "type": "NORMAL"
                }
                """;
        server.expect(ExpectedCount.once(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(serverExpectedBody));
        Payment payment = PaymentFixture.createDefault();

        assertThatThrownBy(() -> paymentClient.requestPaymentConfirm(
                payment.getPaymentKey(), payment.getOrderId(), payment.getAmount()))
                .isInstanceOf(ExternalApiClientException.class);
    }
}
