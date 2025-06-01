package roomescape.utility.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.dto.business.PaymentResult;
import roomescape.exception.ExternalApiConnectionException;
import roomescape.exception.PaymentException;

class TossPaymentClientTest {

    private TossPaymentClient tossPaymentClient;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        tossPaymentClient = new TossPaymentClient(
                restClient, "secret_key", "/payment/authorization");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("결제 서버에 결제 승인 요청을 보낼 수 있다.")
    public class authorizePayment {

        @DisplayName("정상적으로 결제 승인 처리를 할 수 있다.")
        @Test
        void canAuthorizePayment() {
            // given
            String mockResponseBody = "{\"orderId\": \"213wer123\", \"paymentKey\": \"dsf1234\", \"totalAmount\": 1000}";
            MockResponse expectedResponse = new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody(mockResponseBody);
            mockWebServer.enqueue(expectedResponse);

            // when
            PaymentResult actualResult = tossPaymentClient.authorizePayment("213wer123", "dsf1234", 1000);

            // then
            assertAll(
                    () -> assertThat(actualResult.orderId()).isEqualTo("213wer123"),
                    () -> assertThat(actualResult.paymentKey()).isEqualTo("dsf1234"),
                    () -> assertThat(actualResult.totalAmount()).isEqualTo(1000)
            );
        }

        @DisplayName("결제 서버에 연결에 실패할 경우 예외를 발생시킨다.")
        @Test
        void occurExceptionWhenConnectionFail() {
            // given
            RestClient restClient = RestClient.builder()
                    .baseUrl("http://unkown-domain")
                    .build();

            tossPaymentClient = new TossPaymentClient(
                    restClient, "secret_key", "/payment/authorization");

            // when & then
            assertThatThrownBy(() -> tossPaymentClient.authorizePayment("asdf", "asdf", 1234))
                    .isInstanceOf(ExternalApiConnectionException.class)
                    .hasMessage("토스 결제 서버에 연결이 실패하였습니다.");
        }

        @DisplayName("타임아웃이 발생할 경우 예외를 발생킨다.")
        @Test
        void occurExceptionWhenTimeout() {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(Duration.ofSeconds(1));
            requestFactory.setReadTimeout(Duration.ofSeconds(2));

            RestClient restClient = RestClient.builder()
                    .baseUrl("http://unkown-domain")
                    .requestFactory(requestFactory)
                    .build();

            MockResponse expectedResponse = new MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody("Timeout 발생")
                    .setBodyDelay(1500, TimeUnit.NANOSECONDS);
            mockWebServer.enqueue(expectedResponse);

            // when & then
            assertThatThrownBy(() -> tossPaymentClient.authorizePayment("asdf", "asdf", 1234))
                    .isInstanceOf(ExternalApiConnectionException.class)
                    .hasMessage("토스 결제 서버에 연결이 실패하였습니다.");
        }

        @DisplayName("4XX 예외응답을 받을 경우 예외를 발생시킨다.")
        @Test
        void occurExceptionWhen4XXResponse() {
            // given
            String responseBody = """
                    {
                        "code": "NOT_FOUND_PAYMENT",
                        "message": "존재하지 않는 결제 입니다."
                    }
                    """;
            MockResponse expectedResponse = new MockResponse()
                    .setResponseCode(404)
                    .addHeader("Content-Type", "application/json")
                    .setBody(responseBody);
            mockWebServer.enqueue(expectedResponse);

            // when & then
            assertThatThrownBy(() -> tossPaymentClient.authorizePayment("asdf", "asdf", 1234))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage("존재하지 않는 결제 입니다.");
        }

        @DisplayName("5XX 예외응답을 받을 경우 예외를 발생시킨다.")
        @Test
        void occurExceptionWhen5XXResponse() {
            // given
            String responseBody = """
                    {
                        "code": "UNKNOWN_PAYMENT_ERROR",
                        "message": "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."
                    }
                    """;
            MockResponse expectedResponse = new MockResponse()
                    .setResponseCode(500)
                    .addHeader("Content-Type", "application/json")
                    .setBody(responseBody);
            mockWebServer.enqueue(expectedResponse);

            // when & then
            assertThatThrownBy(() -> tossPaymentClient.authorizePayment("asdf", "asdf", 1234))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage("토스 결제 서버에서 예상치 못한 예외가 발생했습니다.");
        }
    }
}
