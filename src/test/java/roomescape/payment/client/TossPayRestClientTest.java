package roomescape.payment.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import roomescape.exception.ParsingFailException;
import roomescape.exception.PaymentFailException;
import roomescape.exception.TossPayErrorHandler;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.CancelRequest;
import roomescape.payment.dto.PaymentRequest;

class TossPayRestClientTest {

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultStatusHandler(new TossPayErrorHandler());

    private MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();

    private TossPayRestClient tossPayRestClient = new TossPayRestClient(testBuilder.build());

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @DisplayName("결제가 정상적으로 처리되면 Payment 객체를 반환한다.")
    @Test
    void pay() {
        String expectedBody = """
                {
                  "paymentKey": "paymentKey",
                  "orderId": "orderId",
                  "totalAmount": 1000
                }
                """;
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest("orderId", 1000, "paymentKey");

        Payment result = tossPayRestClient.pay(request);
        assertAll(
                () -> assertThat(result.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(result.getOrderId()).isEqualTo("orderId")
        );
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

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest("orderId", 1000, "paymentKey");

        assertThatCode(() -> tossPayRestClient.pay(request))
                .isInstanceOf(PaymentFailException.class)
                .hasMessage("존재하지 않는 결제 입니다.");
    }

    @DisplayName("결제 취소가 정상적으로 처리되면 CANCELED Payment 객체를 반환한다.")
    @Test
    void cancel() {
        String expectedBody = """
                {
                  "paymentKey": "paymentKey",
                  "orderId": "orderId",
                  "totalAmount": 1000,
                  "status": "CANCELED"
                }
                """;
        server.expect(requestTo("https://api.tosspayments.com/v1/payments/paymentKey/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        CancelRequest request = new CancelRequest("paymentKey", "예약취소");

        Payment result = tossPayRestClient.cancel(request);
        assertAll(
                () -> assertThat(result.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELED)
        );
    }

    @DisplayName("결제 취소에 실패하면 예외가 발생한다.")
    @Test
    void throwExceptionByInvalidCancel() {
        String expectedBody = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/paymentKey/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        CancelRequest request = new CancelRequest("paymentKey", "예약취소");

        assertThatCode(() -> tossPayRestClient.cancel(request))
                .isInstanceOf(PaymentFailException.class)
                .hasMessage("존재하지 않는 결제 입니다.");
    }

    @DisplayName("올바르지 않은 형태의 예외데이터가 응답되면 Parsing 예외가 발생한다.")
    @Test
    void throwParsingException() {
        String expectedBody = """
                {
                  "error": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).body(expectedBody).contentType(MediaType.APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest("orderId", 1000, "paymentKey");

        assertThatCode(() -> tossPayRestClient.pay(request))
                .isInstanceOf(ParsingFailException.class);
    }
}
