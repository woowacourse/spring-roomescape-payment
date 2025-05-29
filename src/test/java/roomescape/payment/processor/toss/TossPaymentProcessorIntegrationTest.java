package roomescape.payment.processor.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.TossPaymentException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class TossPaymentProcessorIntegrationTest {

    @Test
    void 에러응답_4xx을_받으면_커스텀_예외를_던진다() {
        // given
        final RestClient.Builder builder = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(new ObjectMapper()));
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder.build();
        final TossPaymentProcessor processor = new TossPaymentProcessor("SecretKey", restClient);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
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
                .hasMessageContaining("잘못된 시크릿키 연동 정보 입니다.");
    }

    @Test
    void 에러응답_5xx을_받으면_커스텀_예외를_던진다() {
        // given
        final RestClient.Builder builder = RestClient.builder()
                .defaultStatusHandler(new TossPaymentProcessorErrorHandler(new ObjectMapper()));
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final RestClient restClient = builder.build();
        final TossPaymentProcessor processor = new TossPaymentProcessor("SecretKey", restClient);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
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
}
