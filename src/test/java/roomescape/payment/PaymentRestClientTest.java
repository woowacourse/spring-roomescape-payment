package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.response.PaymentApproveResponse;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.error.exception.PaymentClientException;
import roomescape.reservation.error.handler.PaymentResponseErrorHandler;
import roomescape.reservation.service.PaymentRestClient;

class PaymentRestClientTest {

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments")
            .defaultStatusHandler(new PaymentResponseErrorHandler());

    private final MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();

    private final PaymentRestClient paymentRestClient = new PaymentRestClient(
            testBuilder, new PaymentResponseErrorHandler(), "testKey");

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @DisplayName("결제가 승인되면, 결제 정보를 반환한다.")
    @Test
    void approve() {
        // given
        String expectedBody = """
                {
                  "paymentKey": "paymentKey",
                  "orderId": "orderId"
                }
                """;
        postConfirmApi(expectedBody, HttpStatus.OK);

        Payment payment = new Payment("paymentKey", "orderId", 1000L, "NORMAL");

        // when
        PaymentApproveResponse response = paymentRestClient.approve(payment);

        // then
        assertThat(response.paymentKey()).isEqualTo("paymentKey");
        assertThat(response.orderId()).isEqualTo("orderId");
    }

    @DisplayName("결제가 승인되지 않는다면, 예외를 던진다.")
    @Test
    void throwExceptionWhenNotApproved() {
        // given
        String expectedBody = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;
        postConfirmApi(expectedBody, HttpStatus.BAD_REQUEST);

        Payment payment = new Payment("paymentKey", "orderId", 1000L, "NORMAL");

        // when & then
        assertThatThrownBy(() -> paymentRestClient.approve(payment))
                .isInstanceOf(PaymentClientException.class);
    }

    private void postConfirmApi(String expectedBody, HttpStatus expectedStatus) {
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(expectedStatus).body(expectedBody)
                                .contentType(MediaType.APPLICATION_JSON)
                );
    }
}
