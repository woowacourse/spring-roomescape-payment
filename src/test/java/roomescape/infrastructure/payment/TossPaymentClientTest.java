package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;
import roomescape.common.interceptor.RestClientInterceptor;
import roomescape.exception.PaymentException;
import roomescape.infrastructure.payment.toss.TossPaymentClient;
import roomescape.infrastructure.payment.toss.TossRestClientProperties;

@ActiveProfiles("test")
@EnableConfigurationProperties(TossRestClientProperties.class)
@RestClientTest(TossPaymentClient.class)
class TossPaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("토스 결제 승인 요청 API를 호출한다.")
    void confirmPayment() {
        // given
        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "ROOM_ESCAPE_test_order_id", 1000);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                            "paymentKey": "paymentKey",
                            "orderId": "ROOM_ESCAPE_test_order_id",
                            "orderName": "방탈출 예약 1건",
                            "amount": 1000
                        }
                        """, MediaType.APPLICATION_JSON));

        // when
        PaymentResponse result = paymentClient.confirmPayment(paymentInfo);

        // then
        assertAll(() -> assertThat(result.paymentKey()).isEqualTo(paymentInfo.paymentKey()),
                () -> assertThat(result.orderId()).isEqualTo(paymentInfo.orderId()),
                () -> assertThat(result.amount()).isEqualTo(paymentInfo.amount()));
    }

    @ParameterizedTest
    @EnumSource(PaymentErrorCode.class)
    @DisplayName("토스 결제 승인 요청 API 호출 시 에러가 발생하면 PaymentException 예외가 발생한다.")
    void confirmPayment_WhenExceptionThrown(PaymentErrorCode errorCode) {
        // given
        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "ROOM_ESCAPE_test_order_id", 1000);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(errorCode.getStatusCode()).contentType(MediaType.APPLICATION_JSON)
                        .body(String.format("""
                                {
                                    "code": "%s",
                                    "message": "%s"
                                }
                                """, errorCode.name(), errorCode.getMessage())));

        // when & then
        assertThatThrownBy(() -> paymentClient.confirmPayment(paymentInfo)).isInstanceOf(PaymentException.class)
                .matches(exception -> ((PaymentException) exception).getErrorCode() == errorCode);
    }

    @ParameterizedTest
    @CsvSource({
            "OK", "BAD_REQUEST", "INTERNAL_SERVER_ERROR"
    })
    @DisplayName("토스 결제 승인 요청 API 호출 시 특정 응답 코드에서 JSON 형식이 아니면 예외가 발생한다.")
    void processErrorResponse_WhenJsonMappingFail(HttpStatus status) {
        // given
        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "ROOM_ESCAPE_test_order_id", 1000);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("올바르지 않은 JSON 형식의 응답"));

        // when & then
        assertThatThrownBy(() -> paymentClient.confirmPayment(paymentInfo))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.RESPONSE_PARSING_ERROR)
                .hasMessageContaining("외부 API 응답을 변환하는 과정에서 오류가 발생했습니다.");
    }

    @TestConfiguration
    public static class TossClientTestConfig {

        @Bean
        public RestClient.Builder tossTestClientBuilder(PaymentClientProperties properties,
                                                        MockServerRestClientCustomizer mockServerRestClientCustomizer) {

            RestClient.Builder builder = RestClient.builder()
                    .baseUrl(properties.getBaseUrl())
                    .requestInterceptor(new RestClientInterceptor());

            mockServerRestClientCustomizer.customize(builder);

            return builder;
        }
    }
}
