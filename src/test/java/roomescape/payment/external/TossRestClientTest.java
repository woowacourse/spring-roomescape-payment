package roomescape.payment.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.exception.InvalidPaymentException;
import roomescape.payment.exception.TossPaymentErrorHandler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TossRestClientTest {

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultStatusHandler(HttpStatusCode::isError, new TossPaymentErrorHandler());

    private final MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();
    private final TossRestClient tossRestClient = new TossRestClient(testBuilder.build());

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @DisplayName("결제가 정상적으로 승인 처리된다.")
    @Test
    void approvePayment() {
        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess());

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto("paymentKey_test", "orderId123", 10000);

        assertDoesNotThrow(() -> tossRestClient.confirmPayment(paymentRequestDto));
    }

    @DisplayName("결제 승인이 정상 처리되면 PaymentResponseDto를 반환한다.")
    @Test
    void success_confirmPayment() {
        String expectedRequest = """
                {
                    "paymentKey": "paymentKey_test",
                    "orderId": "orderId123",
                    "totalAmount": 10000
                }
                """;
        String expectedResponse = expectedRequest;

        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().json(expectedRequest))
                .andRespond(MockRestResponseCreators.withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto("paymentKey_test", "orderId123", 10000);

        PaymentResponseDto responseDto = tossRestClient.confirmPayment(paymentRequestDto);

        assertAll(
                () -> assertThat(responseDto.paymentKey()).isEqualTo("paymentKey_test"),
                () -> assertThat(responseDto.orderId()).isEqualTo("orderId123"),
                () -> assertThat(responseDto.totalAmount()).isEqualTo(10000)
        );
    }

    @DisplayName("결제 승인 처리가 실패하면 예외가 발생한다.")
    @Test
    void error_failPayment() {
        String expectedResponse = """
                {
                    "code": "NOT_FOUND_PAYMENT",
                    "message": "존재하지 않는 결제 정보 입니다."
                }
                """;

        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(
                        MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST)
                                .body(expectedResponse)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto("paymentKey_test", "orderId123", 10000);

        assertThatThrownBy(() -> tossRestClient.confirmPayment(paymentRequestDto))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("존재하지 않는 결제 정보 입니다.");
    }
}
