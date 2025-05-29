package roomescape.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentClientMockServerTest {

    private MockWebServer mockWebServer;
    private PaymentClient paymentClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        paymentClient = new PaymentClient(restClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("정상 결제 응답 반환한다")
    void confirmPayment() throws Exception {
        // given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");
        PaymentResponse expectedResponse = new PaymentResponse("paymentKey123", 1000, "orderId123", "DONE");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(expectedResponse))
                .addHeader("Content-Type", "application/json"));

        // when
        PaymentResponse result = paymentClient.confirmPayment(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo("/v1/payments/confirm");
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");

        PaymentRequest actual = objectMapper.readValue(
                recordedRequest.getBody().readUtf8(), PaymentRequest.class);
        assertThat(actual).isEqualTo(request);
    }

    @Test
    @DisplayName("UNAUTHORIZED 예외 발생 시 RuntimeException를 던진다")
    void confirmPayment_throwsRuntimeException_whenUnauthorized() {
        // given
        PaymentRequest request = new PaymentRequest("invalidKey", 1000, "orderId123", "paymentType");
        String errorResponse = "{\"message\":\"인증 실패\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())  // HTTP 401
                .setBody(errorResponse)
                .addHeader("Content-Type", "application/json"));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("결제 확인에 실패했습니다.")
                .hasMessageContaining("인증 실패")
                .hasMessageContaining("invalidKey");
    }

    @Test
    @DisplayName("기타 API 예외 발생 시, PaymentApiException 을 던진다")
    void confirmPayment_throwsPaymentApiException_whenOtherError() {
        // given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");
        String errorResponse = "{ \"code\":\"BAD_REQUEST\", \"message\":\"잘못된 요청\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setBody(errorResponse)
                .addHeader("Content-Type", "application/json"));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentApiException.class)
                .hasMessageContaining("결제 Api가 실패하였습니다.")
                .hasMessageContaining("잘못된 요청")
                .hasMessageContaining("BAD_REQUEST");
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException 던진다")
    void confirmPayment_throwsRuntimeException_whenJsonParsingFails() {
        // given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");
        String invalidJsonResponse = "invalid json";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setBody(invalidJsonResponse)
                .addHeader("Content-Type", "application/json"));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("파싱에 실패했습니다.");
    }

    @Test
    @DisplayName("네트워크 연결 실패 시 적절한 예외를 던진다")
    void confirmPayment_throwsException_whenNetworkFails() throws Exception {
        // given
        PaymentRequest request = new PaymentRequest("paymentKey123", 1000, "orderId123", "paymentType");

        // MockWebServer를 미리 종료하여 연결 실패 상황 만들기
        mockWebServer.shutdown();

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(Exception.class);
    }
}
