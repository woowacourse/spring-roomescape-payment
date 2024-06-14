package roomescape.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.config.PaymentConfig;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.PaymentException;

@RestClientTest(PaymentConfig.class)
class PaymentClientTest {
    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @DisplayName("결제가 이상없이 승인되면 올바른 응답을 반환한다.")
    @Test
    void paymentSuccess() {
        PaymentRequest request = new PaymentRequest("randomOrderId", BigDecimal.valueOf(10000), "randomPaymentKey");
        String expectedResponse = """
                {
                    "paymentKey" : "randomPaymentKey",
                    "totalAmount" : 10000
                }
                """;
        mockServer.expect(manyTimes(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        PaymentResponse result = paymentClient.requestPayment(request);
        assertAll(
                () -> assertThat(result.paymentKey()).isEqualTo("randomPaymentKey"),
                () -> assertThat(result.totalAmount()).isEqualTo(BigDecimal.valueOf(10000))
        );
    }

    @DisplayName("결제 승인 요청이 실패하면 예외를 던진다.")
    @Test
    void paymentFailure() {
        PaymentRequest request = new PaymentRequest("randomOrderId", BigDecimal.valueOf(10000), "randomPaymentKey");
        String expectedResponse = """
                {
                    "code": "NOT_FOUND_PAYMENT",
                    "message": "존재하지 않는 결제 입니다."
                }
                """;
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        assertThatThrownBy(() -> paymentClient.requestPayment(request))
                .isInstanceOf(PaymentException.class);
    }
}