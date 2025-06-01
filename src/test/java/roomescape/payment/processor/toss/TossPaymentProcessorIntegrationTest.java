package roomescape.payment.processor.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.TossPaymentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class TossPaymentProcessorIntegrationTest {

    @Test
    void 에러응답_4xx를_받으면_커스텀_예외를_던진다() {
        // given
        final RestClient.Builder builder = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(new ObjectMapper()));
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder.build();
        final String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";
        final TossPaymentProcessor processor = new TossPaymentProcessor("SecretKey", restClient, confirmUrl);

        server.expect(requestTo(confirmUrl))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                    { "code": "INVALID_CARD_EXPIRATION",
                                      "message": "카드 정보를 다시 확인해주세요. (유효기간)" }
                                """)
                );

        // when & then
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(10000, "orderId", "paymentKey");

        assertThatThrownBy(() -> processor.processPayment(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("카드 정보를 다시 확인해주세요. (유효기간)");
    }

    @Test
    void 에러응답_5xx을_받으면_커스텀_예외를_던진다() {
        // given
        final RestClient.Builder builder = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(new ObjectMapper()));
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder.build();
        final String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";
        final TossPaymentProcessor processor = new TossPaymentProcessor("SecretKey", restClient, confirmUrl);

        server.expect(requestTo(confirmUrl))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                    { "code": "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                                    "message": "결제가 완료되지 않았어요. 다시 시도해주세요." }
                                """)
                );

        // when & then
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(10000, "orderId", "invalid-key");

        assertThatThrownBy(() -> processor.processPayment(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제가 완료되지 않았어요. 다시 시도해주세요.");
    }

    @Test
    void 에러응답_400인데_Secret_KEY_에러를_받으면_커스텀_예외를_500상태로_던진다() {
        // given
        final RestClient.Builder builder = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(new ObjectMapper()));
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder.build();
        final String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";
        final TossPaymentProcessor processor = new TossPaymentProcessor("SecretKey", restClient, confirmUrl);

        server.expect(requestTo(confirmUrl))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                    { "code": "INVALID_API_KEY",
                                    "message": "잘못된 시크릿키 연동 정보 입니다." }
                                """)
                );

        // when & then
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(10000, "orderId", "invalid-key");

        assertThatThrownBy(() -> processor.processPayment(request))
                .isInstanceOf(TossPaymentException.class)
                .satisfies(exception -> {
                    TossPaymentException tossPaymentException = (TossPaymentException) exception;
                    assertThat(tossPaymentException.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(tossPaymentException.getMessage()).isEqualTo("잘못된 시크릿키 연동 정보 입니다.");
                });
    }

    @Test
    void 에러응답_403인데_Basic_AUTH_에러를_받으면_커스텀_예외를_500상태로_던진다() {
        // given
        final RestClient.Builder builder = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(new ObjectMapper()));
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder.build();
        final String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";
        final TossPaymentProcessor processor = new TossPaymentProcessor("SecretKey", restClient, confirmUrl);

        server.expect(requestTo(confirmUrl))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                    { "code": "INCORRECT_BASIC_AUTH_FORMAT",
                                    "message": "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요." }
                                """)
                );

        // when & then
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(10000, "orderId", "invalid-key");

        assertThatThrownBy(() -> processor.processPayment(request))
                .isInstanceOf(TossPaymentException.class)
                .satisfies(exception -> {
                    TossPaymentException tossPaymentException = (TossPaymentException) exception;
                    assertThat(tossPaymentException.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(tossPaymentException.getMessage()).isEqualTo("잘못된 요청입니다. ':' 를 포함해 인코딩해주세요.");
                });
    }
}
