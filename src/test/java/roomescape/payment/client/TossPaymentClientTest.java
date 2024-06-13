package roomescape.payment.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@SpringBootTest
class TossPaymentClientTest {

    @MockBean
    private RestClient restClient;

    @MockBean
    private ClientHttpResponse clientHttpResponse;

    @Autowired
    private PaymentClient paymentClient;

    @Test
    @DisplayName("결제를 승인한다.")
    void confirmPayment() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", 1000L, "paymentType");
        PaymentResponse mockResponse = new PaymentResponse("paymentKey", "orderId", OffsetDateTime.now(), 1000L);

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/confirm")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(paymentRequest)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(PaymentResponse.class)).thenReturn(mockResponse);

        // when
        PaymentResponse paymentResponse = paymentClient.confirmPayment(paymentRequest);

        // then
        assertThat(paymentResponse).isNotNull();
        assertThat(paymentResponse.paymentKey()).isEqualTo("paymentKey");
        assertThat(paymentResponse.orderId()).isEqualTo("orderId");
    }

    @Test
    @DisplayName("결제 승인 중 예외가 발생한다.")
    void confirmPaymentWithError() throws IOException {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", 1000L, "paymentType");

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));
        when(clientHttpResponse.getBody()).thenReturn(
                new ByteArrayInputStream("{\"code\": \"ERROR_CODE\", \"message\": \"Error message\"}".getBytes()));

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/confirm")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(paymentRequest)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).then(invocation -> {
            invocation.getArgument(1, ErrorHandler.class).handle(null, clientHttpResponse);
            return null;
        });

        // when & then
        assertThatThrownBy(() -> paymentClient.confirmPayment(paymentRequest))
                .isInstanceOf(RoomEscapeException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_ERROR)
                .hasFieldOrPropertyWithValue("invalidValue",
                        Optional.of("[ErrorCode = ERROR_CODE, ErrorMessage = Error message]"))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("결제를 취소한다.")
    void cancelPayment() {
        // given
        PaymentCancelRequest cancelRequest = new PaymentCancelRequest("paymentKey", 1000L, "cancelReason");
        PaymentCancelResponse mockResponse = new PaymentCancelResponse("DONE", "고객 요청", 1000L, OffsetDateTime.now());

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/{paymentKey}/cancel", cancelRequest.paymentKey())).thenReturn(
                requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(Map.of("cancelReason", cancelRequest.cancelReason()))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(PaymentCancelResponse.class)).thenReturn(mockResponse);

        // when
        PaymentCancelResponse cancelResponse = paymentClient.cancelPayment(cancelRequest);

        // then
        assertThat(cancelResponse).isNotNull();
        assertThat(cancelResponse.cancelStatus()).isEqualTo("DONE");
        assertThat(cancelResponse.cancelReason()).isEqualTo("고객 요청");
    }

    @Test
    @DisplayName("결제 취소 중 예외가 발생한다.")
    void cancelPaymentWithError() throws IOException {
        // given
        PaymentCancelRequest cancelRequest = new PaymentCancelRequest("paymentKey", 1000L, "cancelReason");

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatusCode.valueOf(500));
        when(clientHttpResponse.getBody()).thenReturn(
                new ByteArrayInputStream("{\"code\": \"ERROR_CODE\", \"message\": \"Error message\"}".getBytes()));

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/payments/{paymentKey}/cancel", cancelRequest.paymentKey())).thenReturn(
                requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(Map.of("cancelReason", cancelRequest.cancelReason()))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any())).then(invocation -> {
            invocation.getArgument(1, ErrorHandler.class).handle(null, clientHttpResponse);
            return null;
        });

        // when & then
        assertThatThrownBy(() -> paymentClient.cancelPayment(cancelRequest))
                .isInstanceOf(RoomEscapeException.class)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.PAYMENT_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("invalidValue",
                        Optional.of("[ErrorCode = ERROR_CODE, ErrorMessage = Error message]"))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
