package roomescape.payment.service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.ConnectTimeOutException;
import roomescape.common.exception.PaymentServerException;
import roomescape.common.exception.ReadTimeOutException;
import roomescape.common.exception.error.PaymentErrorCode;
import roomescape.common.exception.PaymentException;
import roomescape.payment.service.dto.PaymentClientErrorResponse;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;

class PaymentServerTest {

    public static final int TIME_OUT_SECOND = 1;
    private static final String TEST_SECRET_KEY = "test_secret_key_123";

    private PaymentRestClient paymentRestClient;
    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        objectMapper = new ObjectMapper();
        mockWebServer.start();

        final String baseUrl = mockWebServer.url("/").toString();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(TIME_OUT_SECOND));
        requestFactory.setReadTimeout(Duration.ofSeconds(TIME_OUT_SECOND));

        final RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        paymentRestClient = new PaymentRestClient(restClient, TEST_SECRET_KEY);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("결제 승인 요청이 성공한다")
    void confirmPaymentSuccess() throws Exception {
        final PaymentConfirmRequest requestDto = new PaymentConfirmRequest("orderId123", "15000L", 1500L);
        final PaymentClientResponse expectedResponseDto = new PaymentClientResponse(
                "paymentKey", "orderId", 1500L, "DONE",
                "2022-06-08T15:40:09+09:0", "2022-06-08T15:40:49+09:00"
        );
        final String expectedJsonResponse = objectMapper.writeValueAsString(expectedResponseDto);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(expectedJsonResponse)
        );

        PaymentClientResponse actualResponse = paymentRestClient.confirm(requestDto);

        assertThat(actualResponse).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "NOT_FOUND_TERMINAL_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    })
    @DisplayName("결제 승인 요청 4xx 응답의 원인이 서버에 있다면 500 예외를 발생시킨다")
    void confirmPaymentClientErrorToServerError(String code) throws JsonProcessingException {
        final PaymentConfirmRequest requestDto = new PaymentConfirmRequest(
                "paymentKey", "orderId", 1000L);
        final PaymentClientErrorResponse errorResponseDto = new PaymentClientErrorResponse(
                code, null, null);
        final String errorJsonResponse = objectMapper.writeValueAsString(errorResponseDto);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(errorJsonResponse)
        );

        assertThatThrownBy(() -> paymentRestClient.confirm(requestDto))
                .isInstanceOf(PaymentServerException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.SERVER_ERROR);
    }

    @Test
    @DisplayName("일반적인 4xx 예외라면 4xx 예외를 던진다")
    void confirmPaymentClientError() throws JsonProcessingException {
        final PaymentConfirmRequest requestDto = new PaymentConfirmRequest(
                "paymentKey", "orderId", 1000L);
        final PaymentClientErrorResponse errorResponseDto = new PaymentClientErrorResponse(
                "ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다.", null);
        final String errorJsonResponse = objectMapper.writeValueAsString(errorResponseDto);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(errorJsonResponse)
        );

        assertThatThrownBy(() -> paymentRestClient.confirm(requestDto))
                .isInstanceOf(PaymentException.class)
                .hasMessage("이미 처리된 결제 입니다.")
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.CLIENT_ERROR);
    }

    @Test
    @DisplayName("일반적인 5xx 예외라면 500 예외를 발생시킨다")
    void confirmPaymentServerError() throws JsonProcessingException {
        final PaymentConfirmRequest requestDto = new PaymentConfirmRequest(
                "paymentKey", "orderId", 1000L);
        final PaymentClientErrorResponse errorResponseDto = new PaymentClientErrorResponse(
                "FAILED_INTERNAL_SYSTEM_PROCESSING", "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.", null);
        final String errorJsonResponse = objectMapper.writeValueAsString(errorResponseDto);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(errorJsonResponse)
        );

        assertThatThrownBy(() -> paymentRestClient.confirm(requestDto))
                .isInstanceOf(PaymentException.class)
                .hasMessage("내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.")
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_SERVER_ERROR);
    }

    @Test
    @DisplayName("ReadTimeout 초과시 예외가 발생한다")
    void readTimeout() {
        final PaymentConfirmRequest requestDto = new PaymentConfirmRequest(
                "paymentKey", "orderId", 1000L);

        mockWebServer.enqueue(
                new MockResponse()
                        .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        );

        assertThatThrownBy(() -> paymentRestClient.confirm(requestDto))
                .isInstanceOf(ReadTimeOutException.class)
                .hasMessage("Read timed out");
    }

    @Test
    @DisplayName("ConnectTimeout 초과시 예외가 발생한다")
    void connectTimeout(){
        final PaymentConfirmRequest requestDto = new PaymentConfirmRequest(
                "paymentKey", "orderId", 1000L);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(TIME_OUT_SECOND));

        final RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("http://10.255.255.1:8080").toString())
                .requestFactory(requestFactory)
                .build();

        paymentRestClient = new PaymentRestClient(restClient, TEST_SECRET_KEY);

        assertThatThrownBy(() -> paymentRestClient.confirm(requestDto))
                .isInstanceOf(ConnectTimeOutException.class)
                .hasMessage("Connect timed out");
    }
}
