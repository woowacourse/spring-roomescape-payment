package roomescape.infra;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import roomescape.config.PaymentRestClientConfig;
import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentException;

@RestClientTest(value = PaymentRestClient.class)
@Import(PaymentRestClientConfig.class)
class PaymentRestClientTest {

    private static final String url = "https://api.tosspayments.com/v1/payments/confirm";

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private PaymentRestClient paymentRestClient;

    @Test
    @DisplayName("serverError에 해당하는 에러 발생시 PaymentException으로 변환하고, 그 상태 코드는 INTERNAL_SERVER_ERROR 이다.")
    void serverErrorHandling() {
        String body = """
                {
                    "code": "INVALID_API_KEY",
                    "message": "잘못된 시크릿키 연동 정보 입니다."
                }
                """;
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(StandardCharsets.UTF_8), HttpStatus.BAD_REQUEST);

        server.expect(MockRestRequestMatchers.requestTo(url))
                .andRespond(request -> response);

        PaymentRequest paymentRequest = new PaymentRequest(
                1L, "paymentKey", "orderId", BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> paymentRestClient.requestPaymentApproval(paymentRequest))
                .isExactlyInstanceOf(PaymentException.class)
                .hasMessage("결제 서버에 문제가 발생했습니다.")
                .extracting(exception -> ((PaymentException) exception).getStatus())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("clientError에 해당하는 에러 발생시 PaymentException으로 변환하고, 그 상태 코드는 BAD_REQUEST 이다.")
    void clientErrorHandling() {
        String body = """
                {
                    "code": "INVALID_CARD_EXPIRATION",
                    "message": "카드 정보를 다시 확인해주세요. (유효기간)"
                }
                """;
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(StandardCharsets.UTF_8), HttpStatus.BAD_REQUEST);

        server.expect(MockRestRequestMatchers.requestTo(url))
                .andRespond(request -> response);

        PaymentRequest paymentRequest = new PaymentRequest(
                1L, "paymentKey", "orderId", BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> paymentRestClient.requestPaymentApproval(paymentRequest))
                .isExactlyInstanceOf(PaymentException.class)
                .hasMessage("카드 정보를 다시 확인해주세요. (유효기간)")
                .extracting(exception -> ((PaymentException) exception).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
