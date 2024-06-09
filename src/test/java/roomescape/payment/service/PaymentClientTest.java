package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.service.PaymentClientTest.TestConfig;

@RestClientTest(value = TestConfig.class)
class PaymentClientTest {
    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private PaymentClient paymentClient;

    @DisplayName("결제 확인 되었을 경우 정상 처리한다.")
    @Test
    void confirmPaymentTest() {
        PaymentConfirmRequest request = new PaymentConfirmRequest(
                "testPaymentKey", "TestOrderId", BigDecimal.valueOf(1000));
        mockRestServiceServer.expect(requestTo("/confirm"))
                .andRespond(withSuccess());

        assertThatCode(() -> paymentClient.confirmPayment(request)).doesNotThrowAnyException();
    }

    @DisplayName("결제 확인 오류가 발생한 경우 예외를 던진다.")
    @Test
    void confirmPaymentTest_whenNotConfirmed() {
        PaymentConfirmRequest request = new PaymentConfirmRequest(
                "testPaymentKey", "TestOrderId", BigDecimal.valueOf(1000));
        String errorResponse = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        mockRestServiceServer.expect(requestTo("/confirm"))
                .andRespond(withBadRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("존재하지 않는 결제 입니다.");
    }

    @DisplayName("결제 확인시 인증오류 발생한 경우 예외를 던진다.")
    @Test
    void confirmPaymentTest_whenUnauthorized() {
        PaymentConfirmRequest request = new PaymentConfirmRequest("testPaymentKey", "TestOrderId",
                BigDecimal.valueOf(1000));

        String errorResponse = """
                {
                  "code": "UNAUTHORIZED_KEY",
                  "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                }
                """;
        mockRestServiceServer.expect(requestTo("/confirm"))
                .andRespond(withUnauthorizedRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentUnauthorizedException.class);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        PaymentClient paymentService(RestClient.Builder builder) {
            return new PaymentClient(builder.build(), "test");
        }
    }
}
