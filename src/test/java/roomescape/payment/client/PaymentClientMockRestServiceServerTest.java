package roomescape.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.payment.client.config.TestPaymentConfiguration;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResult;
import roomescape.payment.exception.PaymentInternalServerException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(PaymentClient.class)
@Import(TestPaymentConfiguration.class)
class PaymentClientMockRestServiceServerTest {

    private static final String PATH = "/payments/confirm";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private PaymentClient paymentClient;

    @Value("${toss.payment.base-url}")
    private String URL;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("정상 결제 응답 반환한다")
    void confirmPayment() throws Exception {
        // given
        PaymentRequest request = getRequest("paymentKey123");
        PaymentResult expectedResponse = new PaymentResult("paymentKey123", 1000, "orderId123", "DONE");

        mockServer.expect(requestTo(URL + PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Content-Type", "application/json"))
                .andExpect(jsonPath("$.paymentKey").value("paymentKey123"))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.orderId").value("orderId123"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(expectedResponse)));

        // when
        PaymentResult result = paymentClient.confirmPayment(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.paymentKey()).isEqualTo("paymentKey123");
        assertThat(result.amount()).isEqualTo(1000);
        assertThat(result.orderId()).isEqualTo("orderId123");
        assertThat(result.paymentType()).isEqualTo("DONE");

        mockServer.verify();
    }

    @Test
    @DisplayName("서버 에러 코드에 해당하는 예외 발생 시 PaymentInternalServerException 던진다")
    void confirmPayment_whenErrorCodeForServer() {
        // given
        String invalidKey = "invalidKey";
        PaymentRequest request = getRequest(invalidKey);
        String errorResponse = "{\"code\":\"UNAUTHORIZED_KEY\",\"message\":\"인증 실패\"}";

        mockServer.expect(requestTo(URL + PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.paymentKey").value(invalidKey))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentInternalServerException.class)
                .hasMessageContaining("결제 승인 API 호출 실패했습니다.")
                .hasMessageContaining("UNAUTHORIZED_KEY");

        mockServer.verify();
    }

    private PaymentRequest getRequest(String paymentKey) {
        return new PaymentRequest(paymentKey, 1000, "orderId123", "paymentType");
    }
}
