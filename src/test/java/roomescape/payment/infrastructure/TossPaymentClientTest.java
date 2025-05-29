package roomescape.payment.infrastructure;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.payment.exception.PaymentRequestException;
import roomescape.payment.exception.RequestPaymentErrorHandler;
import roomescape.reservation.dto.PaymentRequest;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TossPaymentClientTest {

    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";

    private final RestClient.Builder testBuilder = RestClient.builder()
        .baseUrl(BASE_URL)
        .defaultStatusHandler(new RequestPaymentErrorHandler(new ObjectMapper()));

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

        PaymentRequest request = new PaymentRequest("paymentKey", "orderId", 1000);

        assertDoesNotThrow(() -> tossPaymentClient.requestPayment(request));
    }

    @DisplayName("결제에 실패하면 예외가 발생한다.")
    @Test
    void throwException() {
        String expectedBody = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        server.expect(requestTo(BASE_URL + "/confirm"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(
                withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest("paymentKey", "orderId", 1000);

        assertThatCode(() -> tossPaymentClient.requestPayment(request))
            .isInstanceOf(PaymentRequestException.class)
            .hasMessage("존재하지 않는 결제 입니다.");
    }
}
