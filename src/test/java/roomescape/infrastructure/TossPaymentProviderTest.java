package roomescape.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static roomescape.domain.payment.PaymentFailCode.EXTERNAL_SERVER_PROCESSING;
import static roomescape.domain.payment.PaymentFailCode.INVALID_AUTH_CREDENTIALS;

import java.net.SocketTimeoutException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentFailedException;

@RestClientTest(PaymentProvider.class)
@Import(TossPaymentProviderConfig.class)
class TossPaymentProviderTest {

    private static final String EXPECTED_CONFIRM_URI = "https://api.tosspayments.com/v1/payments/confirm";

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;
    private PaymentProvider paymentProvider;

    @BeforeEach
    public void setup() {
        paymentProvider = new TossPaymentProvider(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("결제 승인 API 스펙에 맞게 HTTP 요청을 보낸다.")
    void requestToConfirm() {
        // given
        var request = new PaymentRequest("a", "1", 1000);

        server.expect(requestTo(EXPECTED_CONFIRM_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess());

        // when
        paymentProvider.confirm(request);

        // then
        server.verify();
    }

    @Test
    @DisplayName("토스 서버와 연결에 실패할 때 최대 3번까지 연결을 시도한다.")
    void requestRetriesThreeTimesIfTimeout() {
        // given
        var request = new PaymentRequest("a", "1", 1000);
        var tryCount = 3;

        server.expect(times(tryCount), requestTo(EXPECTED_CONFIRM_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(req -> {
                throw new SocketTimeoutException();
            });

        // when
        try {
            paymentProvider.confirm(request);
        } catch (PaymentFailedException ignore) {
        }

        // then
        server.verify();
    }

    @Test
    @DisplayName("HTTP 요청이 토스 서버와의 연결에 실패하면 결제 실패 예외가 발생한다.")
    void requestTimeout() {
        // given
        var request = new PaymentRequest("a", "1", 1000);

        server.expect(times(3), requestTo(EXPECTED_CONFIRM_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(req -> {
                throw new SocketTimeoutException();
            });

        // when
        assertThatThrownBy(() -> paymentProvider.confirm(request))

            // then
            .isInstanceOf(PaymentFailedException.class)
            .satisfies(e -> {
                var paymentFailedException = (PaymentFailedException) e;
                assertThat(paymentFailedException.causedByExternalServer()).isTrue();
            });
    }

    @Test
    @DisplayName("결제 승인에 성공한다.")
    void confirmPaymentSucceeded() {
        // given
        var request = new PaymentRequest("a", "1", 1000);
        var response = """
            {
                "paymentKey": "a",
                "orderId": 1,
                "orderName": "name",
                "totalAmount": 1000
            }
            """;

        server.expect(requestTo(EXPECTED_CONFIRM_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        // when
        var paymentDetails = paymentProvider.confirm(request);

        // then
        assertAll(
            () -> assertThat(paymentDetails.confirmation()).isNotNull(),
            () -> assertThat(paymentDetails.isFailed()).isFalse(),
            () -> assertThat(paymentDetails.failure()).isNull()
        );
    }

    @ParameterizedTest
    @DisplayName("결제 승인 실패하면 실패에 관한 결제 세부사항을 얻는다")
    @MethodSource("confirmPaymentFailedSource")
    void confirmPaymentFailed(final String response, final PaymentFailCode expectedStatusCode) {
        // given
        var request = new PaymentRequest("a", "1", 1000);

        server.expect(requestTo(EXPECTED_CONFIRM_URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withBadRequest().contentType(MediaType.APPLICATION_JSON).body(response));

        // when
        var paymentDetails = paymentProvider.confirm(request);

        // then
        assertAll(
            () -> assertThat(paymentDetails.confirmation()).isNull(),
            () -> assertThat(paymentDetails.isFailed()).isTrue(),
            () -> assertThat(paymentDetails.failure().code()).isEqualTo(expectedStatusCode)
        );
    }

    private static Stream<Arguments> confirmPaymentFailedSource() {
        return Stream.of(
            Arguments.of("""
                {
                    "code" : "INVALID_API_KEY",
                    "message" : "Invalid API key"
                }
            """, INVALID_AUTH_CREDENTIALS
            ),
            Arguments.of("""
                {
                    "code" : "UNAUTHORIZED_KEY",
                    "message" : "Unauthorized key"
                }
            """, INVALID_AUTH_CREDENTIALS
            ),
            Arguments.of("""
                {
                    "code" : "INCORRECT_BASIC_AUTH_FORMAT",
                    "message" : "Incorrect auth format"
                }
            """, INVALID_AUTH_CREDENTIALS
            ),
            Arguments.of("""
                {
                    "code" : "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                    "message" : "Failed payment internal system processing"
                }
            """, EXTERNAL_SERVER_PROCESSING
            ),
            Arguments.of("""
                {
                    "code" : "FAILED_INTERNAL_SYSTEM_PROCESSING",
                    "message" : "Failed internal system processing"
                }
            """, EXTERNAL_SERVER_PROCESSING
            ),
            Arguments.of("""
                {
                    "code" : "UNKNOWN_PAYMENT_ERROR",
                    "message" : "Unknown payment error"
                }
            """, EXTERNAL_SERVER_PROCESSING
            )
        );
    }
}
