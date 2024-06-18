package roomescape.payment.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@RestClientTest(TossPaymentClient.class)
class TossPaymentClientTest {

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    @DisplayName("결제를 승인한다.")
    void confirmPayment() {
        // given
        mockServer.expect(requestTo("/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(SampleTossPaymentConst.paymentRequestJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SampleTossPaymentConst.confirmJson));

        // when
        PaymentRequest paymentRequest = SampleTossPaymentConst.paymentRequest;
        PaymentResponse paymentResponse = tossPaymentClient.confirmPayment(paymentRequest);

        // then
        assertThat(paymentResponse.paymentKey()).isEqualTo(paymentRequest.paymentKey());
        assertThat(paymentResponse.orderId()).isEqualTo(paymentRequest.orderId());
    }

    @Test
    @DisplayName("결제를 취소한다.")
    void cancelPayment() {
        // given
        mockServer.expect(requestTo("/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(SampleTossPaymentConst.cancelRequestJson))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SampleTossPaymentConst.cancelJson));

        // when
        PaymentCancelRequest cancelRequest = SampleTossPaymentConst.cancelRequest;
        PaymentCancelResponse paymentCancelResponse = tossPaymentClient.cancelPayment(cancelRequest);

        // then
        assertThat(paymentCancelResponse.cancelStatus()).isEqualTo("DONE");
        assertThat(paymentCancelResponse.cancelReason()).isEqualTo(cancelRequest.cancelReason());
    }

    @Test
    @DisplayName("결제 승인 중 400 에러가 발생한다.")
    void confirmPaymentWithError() {
        // given
        mockServer.expect(requestTo("/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(SampleTossPaymentConst.paymentRequestJson))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SampleTossPaymentConst.tossPaymentErrorJson));

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirmPayment(SampleTossPaymentConst.paymentRequest))
                .isInstanceOf(RoomEscapeException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_ERROR)
                .hasFieldOrPropertyWithValue("invalidValue",
                        Optional.of("[ErrorCode = ERROR_CODE, ErrorMessage = Error message]"))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("결제 취소 중 500 에러가 발생한다.")
    void cancelPaymentWithError() {
        // given
        mockServer.expect(requestTo("/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(SampleTossPaymentConst.cancelRequestJson))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SampleTossPaymentConst.tossPaymentErrorJson));

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.cancelPayment(SampleTossPaymentConst.cancelRequest))
                .isInstanceOf(RoomEscapeException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("invalidValue",
                        Optional.of("[ErrorCode = ERROR_CODE, ErrorMessage = Error message]"))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
