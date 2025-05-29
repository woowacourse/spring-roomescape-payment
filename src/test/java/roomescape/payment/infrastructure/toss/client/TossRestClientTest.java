package roomescape.payment.infrastructure.toss.client;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.toss.exception.TossInternalException;
import roomescape.payment.infrastructure.toss.exception.TossPaymentApprovalFailedException;

class TossRestClientTest {
    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com");
    private final String orderId = "test";
    private final BigDecimal amount = BigDecimal.valueOf(1000);
    private final String paymentKey = "test";
    private MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();
    private TossRestClient tossRestClient = new TossRestClient(testBuilder.build(), new ObjectMapper());

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @DisplayName("결제 승인 API 호출 - 성공")
    @Test
    void approve_success() {
        // given
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when & then
        assertDoesNotThrow(() -> tossRestClient.approve(orderId, amount, paymentKey));
    }

    @DisplayName("결제 승인 API 호출 - 400에러이면서 메세지 노출이 가능한 경우 TossPaymentApprovalFailedException 발생")
    @Test
    void approve_400ErrorWithMessage() {
        String expectedBody = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        assertThatCode(() -> tossRestClient.approve(orderId, amount, paymentKey))
                .isInstanceOf(TossPaymentApprovalFailedException.class);
    }

    @DisplayName("결제 승인 API 호출 - 400에러이면서 메세지 노출이 불가능한 경우 TossInternalException 발생")
    @Test
    void approve_400ErrorWithoutMessage() {
        String expectedBody = """
                {
                  "code": "INVALID_API_KEY",
                  "message": "일치하지 않는 키입니다"
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        assertThatCode(() -> tossRestClient.approve(orderId, amount, paymentKey))
                .isInstanceOf(TossInternalException.class);
    }

    @DisplayName("결제 승인 API 호출 - 500에러의 경우 TossPaymentApprovalFailedException 발생")
    @Test
    void approve_500Error() {
        String expectedBody = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.INTERNAL_SERVER_ERROR).body(expectedBody)
                                .contentType(MediaType.APPLICATION_JSON));

        assertThatCode(() -> tossRestClient.approve(orderId, amount, paymentKey))
                .isInstanceOf(TossPaymentApprovalFailedException.class);
    }

}