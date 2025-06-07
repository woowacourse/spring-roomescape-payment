package roomescape.payment.infrastructure;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.TossPaymentRequest;
import roomescape.payment.exception.TossPaymentClientException;
import roomescape.payment.exception.TossPaymentErrorHandler;
import roomescape.payment.exception.TossPaymentServerException;

class TossPaymentClientTest {

    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl(BASE_URL)
            .defaultStatusHandler(new TossPaymentErrorHandler());

    private final MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();
    private final TossPaymentClient tossPaymentClient = new TossPaymentClient(testBuilder.build());

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @DisplayName("결제를 요청한다.")
    @Test
    void confirmPayment() {
        server.expect(requestTo(BASE_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        TossPaymentRequest request = new TossPaymentRequest("paymentKey", "orderId", 1000);

        assertDoesNotThrow(() -> tossPaymentClient.requestPayment(request));
    }

    @DisplayName("클라이언트가 결제에 실패하면 클라이언트 예외가 발생한다.")
    @Test
    void throwPaymentClientException() {
        String expectedBody = """
                {
                  "code": "ALREADY_PROCESSED_PAYMENT",
                  "message": "이미 처리된 결제 입니다."
                }
                """;

        server.expect(requestTo(BASE_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        TossPaymentRequest request = new TossPaymentRequest("paymentKey", "orderId", 1000);

        assertThatCode(() -> tossPaymentClient.requestPayment(request))
                .isInstanceOf(TossPaymentClientException.class)
                .hasMessage("이미 처리된 결제 입니다.");
    }

    @DisplayName("서버가 결제 처리에 실패하면 서버 예외가 발생한다.")
    @Test
    void throwPaymentServerException() {
        String expectedBody = """
                {
                  "code": "INVALID_API_KEY",
                  "message": "잘못된 시크릿키 연동 정보 입니다."
                }
                """;

        server.expect(requestTo(BASE_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        TossPaymentRequest request = new TossPaymentRequest("paymentKey", "orderId", 1000);

        assertThatCode(() -> tossPaymentClient.requestPayment(request))
                .isInstanceOf(TossPaymentServerException.class)
                .hasMessage("결제 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
