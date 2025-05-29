package roomescape.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static roomescape.domain.payment.PaymentStatusCode.FAILED_INTERNAL_PROCESSING;
import static roomescape.domain.payment.PaymentStatusCode.INVALID_AUTH_CREDENTIALS;

import java.util.stream.Stream;
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
import org.springframework.test.web.client.response.MockRestResponseCreators;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatusCode;

@RestClientTest(PaymentProvider.class)
@Import(TossPaymentConfig.class)
class TossPaymentProviderTest {

    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private PaymentProvider paymentProvider;

    @Test
    @DisplayName("결제 승인 API 스펙에 맞게 HTTP 요청을 보낸다.")
    void requestToConfirm() {
        // given
        var request = new PaymentRequest("a", "1", 1000);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess());

        // when
        paymentProvider.confirm(request);

        // then
        server.verify();
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

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.APPLICATION_JSON));

        // when
        var paymentDetails = paymentProvider.confirm(request);

        // then
        assertAll(
            () -> assertThat(paymentDetails.confirmation()).isNotNull(),
            () -> assertThat(paymentDetails.isFailed()).isFalse(),
            () -> assertThat(paymentDetails.status().code()).isEqualTo(PaymentStatusCode.SUCCEEDED_PAYMENT)
        );
    }

    @ParameterizedTest
    @DisplayName("결제 승인 실패하면 실패에 관한 결제 세부사항을 얻는다")
    @MethodSource("confirmPaymentFailedSource")
    void confirmPaymentFailed(final String response, final PaymentStatusCode expectedStatusCode) {
        // given
        var request = new PaymentRequest("a", "1", 1000);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withBadRequest().contentType(MediaType.APPLICATION_JSON).body(response));

        // when
        var paymentDetails = paymentProvider.confirm(request);

        // then
        assertAll(
            () -> assertThat(paymentDetails.confirmation()).isNull(),
            () -> assertThat(paymentDetails.isFailed()).isTrue(),
            () -> assertThat(paymentDetails.status().code()).isEqualTo(expectedStatusCode)
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
            """, FAILED_INTERNAL_PROCESSING
            ),
            Arguments.of("""
                {
                    "code" : "FAILED_INTERNAL_SYSTEM_PROCESSING",
                    "message" : "Failed internal system processing"
                }
            """, FAILED_INTERNAL_PROCESSING
            ),
            Arguments.of("""
                {
                    "code" : "UNKNOWN_PAYMENT_ERROR",
                    "message" : "Unknown payment error"
                }
            """, FAILED_INTERNAL_PROCESSING
            )
        );
    }
}
