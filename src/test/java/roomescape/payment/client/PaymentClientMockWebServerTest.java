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
import roomescape.payment.dto.PaymentResult;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentInternalServerException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentClientMockWebServerTest {

    private MockWebServer mockWebServer;
    private TossPaymentClient paymentClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        paymentClient = new TossPaymentClient(restClient, objectMapper);
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
        PaymentResult expectedResponse = new PaymentResult("paymentKey123", 1000, "orderId123", "DONE");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(expectedResponse))
                .addHeader("Content-Type", "application/json"));

        // when
        PaymentResult result = paymentClient.confirmPayment(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo("/payments/confirm");
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");

        PaymentRequest actual = objectMapper.readValue(
                recordedRequest.getBody().readUtf8(), PaymentRequest.class);
        assertThat(actual).isEqualTo(request);
    }

    @Test
    @DisplayName("서버 에러 코드에 해당하는 예외 발생 시 PaymentInternalServerException 던진다")
    void confirmPayment_whenErrorCodeForServer() {
        // given
        PaymentRequest request = new PaymentRequest("invalidKey", 1000, "orderId123", "paymentType");
        String errorResponse = "{\"code\":\"UNAUTHORIZED_KEY\",\"message\":\"인증 실패\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
                .setBody(errorResponse)
                .addHeader("Content-Type", "application/json"));

        // when
        // then
        assertThatThrownBy(() -> paymentClient.confirmPayment(request))
                .isInstanceOf(PaymentInternalServerException.class)
                .hasMessageContaining("결제 승인 API 호출 실패했습니다.")
                .hasMessageContaining("UNAUTHORIZED_KEY");
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
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("결제 승인 API 호출 실패했습니다.")
                .hasMessageContaining("BAD_REQUEST");
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
