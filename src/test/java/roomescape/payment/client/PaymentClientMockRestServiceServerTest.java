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
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentApiException;

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

    private static final String PATH = "/v1/payments/confirm";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private PaymentClient paymentClient;

    @Value("${payment.api.base-url}")
    private String URL;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("정상 결제 응답 반환한다")
    void confirmPayment() throws Exception {
        // given
        PaymentRequest request = getRequest("paymentKey123");
        PaymentResponse expectedResponse = new PaymentResponse("paymentKey123", 1000, "orderId123", "DONE");

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
        PaymentResponse result = paymentClient.confirmPayment(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.paymentKey()).isEqualTo("paymentKey123");
        assertThat(result.amount()).isEqualTo(1000);
        assertThat(result.orderId()).isEqualTo("orderId123");
        assertThat(result.status()).isEqualTo("DONE");

        mockServer.verify();
    }

    @Test
    @DisplayName("UNAUTHORIZED 예외 발생 시 RuntimeException를 던진다")
    void confirmPayment_throwsRuntimeException_whenUnauthorized() {
        // given
        String invalidKey = "invalidKey";
        PaymentRequest request = getRequest(invalidKey);
        String errorResponse = "{\"message\":\"인증 실패\"}";

        mockServer.expect(requestTo(URL + PATH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.paymentKey").value(invalidKey))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("결제 확인에 실패했습니다.")
                .hasMessageContaining("인증 실패")
                .hasMessageContaining(invalidKey);

        mockServer.verify();
    }

    @Test
    @DisplayName("기타 API 예외 발생 시, PaymentApiException 을 던진다")
    void confirmPayment_throwsPaymentApiException_whenOtherError() {
        // given
        PaymentRequest request = getRequest("paymentKey123");
        String errorResponse = "{ \"code\":\"BAD_REQUEST\", \"message\":\"잘못된 요청\"}";

        mockServer.expect(requestTo(URL + PATH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentApiException.class)
                .hasMessageContaining("결제 Api가 실패하였습니다.")
                .hasMessageContaining("잘못된 요청")
                .hasMessageContaining("BAD_REQUEST");

        mockServer.verify();
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException 던진다")
    void confirmPayment_throwsRuntimeException_whenJsonParsingFails() {
        // given
        PaymentRequest request = getRequest("paymentKey123");
        String invalidJsonResponse = "invalid json";

        mockServer.expect(requestTo(URL + PATH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(invalidJsonResponse));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("파싱에 실패했습니다.");

        mockServer.verify();
    }

    @Test
    @DisplayName("네트워크 연결 실패 시 적절한 예외를 던진다")
    void confirmPayment_throwsException_whenNetworkFails() {
        // given
        PaymentRequest request = getRequest("paymentKey123");

        mockServer.expect(requestTo(URL + PATH))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        // when & then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(Exception.class);

        mockServer.verify();
    }

    private static PaymentRequest getRequest(String paymentKey) {
        return new PaymentRequest(paymentKey, 1000, "orderId123", "paymentType");
    }
}
