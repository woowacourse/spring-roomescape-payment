package roomescape.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentErrorHandler;
import roomescape.exception.PaymentException;

@SpringBootTest
class TossPaymentClientTest {

    @Value("${toss.widget.secretKey}")
    private String widgetSecretKey;

    @Autowired
    private PaymentErrorHandler paymentErrorHandler;

    private MockRestServiceServer server;

    private PaymentClient paymentClient;

    @BeforeEach
    public void setUp() {
        RestClient.Builder testBuilder = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .defaultStatusHandler(paymentErrorHandler);
        server = MockRestServiceServer.bindTo(testBuilder).build();
        paymentClient = new TossPaymentClient(testBuilder.build(), widgetSecretKey);
    }

    @Test
    @DisplayName("결제가 정상적으로 된 경우 예외를 발생하지 않는다.")
    void requestPaymentApproval() {
        // given
        String response = """
                {
                  "paymentKey": "paymentKey",
                  "orderId": "WTESTorderId",
                  "totalAmount": 1000
                }
                """;
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess().body(response).contentType(MediaType.valueOf("application/json")));

        PaymentRequest paymentRequest = new PaymentRequest(2L, "paymentKey", "WTESTorderId", 1000L);

        // when
        Payment payment = paymentClient.requestPaymentApproval(paymentRequest);

        // then
        assertAll(
                () -> assertThat(payment.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(payment.getOrderId()).isEqualTo("WTESTorderId"),
                () -> assertThat(payment.getAmount()).isEqualTo(1000L)
        );
    }

    @Test
    @DisplayName("결제가 실패한 경우 PaymentException을 발생시킨다.")
    void requestPaymentApprovalThrowExceptionWhenNotFoundPayment() {
        // given
        String errorResponse = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withBadRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest paymentRequest = new PaymentRequest(1L, "paymentKey", "WTESTorderId", 1000L);

        // when && then
        assertThatThrownBy(() -> paymentClient.requestPaymentApproval(paymentRequest)).isInstanceOf(PaymentException.class);
    }
}

