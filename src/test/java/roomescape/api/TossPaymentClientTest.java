package roomescape.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import static roomescape.fixture.PaymentFixture.PAYMENT_INFO;
import static roomescape.fixture.PaymentFixture.PAYMENT_REQUEST;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.exception.PaymentException;
import roomescape.exception.response.UserPaymentExceptionResponse;
import roomescape.payment.api.TossPaymentClient;
import roomescape.payment.config.PaymentClientResponseErrorHandler;
import roomescape.payment.dto.CancelReason;
import roomescape.payment.dto.PaymentRequest;

@RestClientTest(TossPaymentClient.class)
class TossPaymentClientTest {

    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private TossPaymentClient tossPaymentClient;
    @MockBean
    private PaymentClientResponseErrorHandler responseErrorHandler;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${security.toss.payment.url}")
    private String url;

    @DisplayName("적합한 인자를 통한 결제 요청 시 성공한다.")
    @Test
    void payment() throws JsonProcessingException {
        String endPoint = "/v1/payments/confirm";
        mockServer
                .expect(requestTo(url + endPoint))
                .andExpect(content().json(objectMapper.writeValueAsString(PAYMENT_REQUEST)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(PAYMENT_INFO), MediaType.APPLICATION_JSON));

        assertThat(tossPaymentClient.payment(PAYMENT_REQUEST)).isEqualTo(PAYMENT_INFO);
        mockServer.verify();
    }

    @DisplayName("적합하지 못한 인자를 통한 결제 요청 시 실패한다.")
    @Test
    void failPayment() throws IOException {
        String endPoint = "/v1/payments/confirm";
        String errorMessage = "적합하지 않은 paymentKey입니다.";
        PaymentRequest invalidPaymentRequest = new PaymentRequest("invalid", "invalidOrderId", BigDecimal.valueOf(1000));

        mockServer
                .expect(requestTo(url + endPoint))
                .andExpect(content().json(objectMapper.writeValueAsString(invalidPaymentRequest)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body(errorMessage));
        when(responseErrorHandler.hasError(any()))
                .thenThrow(new PaymentException(UserPaymentExceptionResponse.of("INVALID_ERROR_CODE", errorMessage)));

        assertThatThrownBy(() -> tossPaymentClient.payment(invalidPaymentRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage(errorMessage);
    }

    @DisplayName("적합한 인자를 통한 결제 취소 시 성공한다.")
    @Test
    void cancel() throws JsonProcessingException {
        String endPoint = "/v1/payments/" + PAYMENT_REQUEST.paymentKey() + "/cancel";
        mockServer
                .expect(requestTo(url + endPoint))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(PAYMENT_INFO), MediaType.APPLICATION_JSON));

        assertThat(tossPaymentClient.cancel(PAYMENT_REQUEST.paymentKey(), new CancelReason("관리자 권한 취소"))).isEqualTo(PAYMENT_INFO);
        mockServer.verify();
    }

    @DisplayName("적합하지 못한 인자를 통한 결제 취소 시 실패한다.")
    @Test
    void failCancel() throws IOException {
        PaymentRequest invalidPaymentRequest = new PaymentRequest("invalid", "invalidOrderId", BigDecimal.valueOf(1000));
        String endPoint = "/v1/payments/" + invalidPaymentRequest.paymentKey() + "/cancel";
        String errorMessage = "적합하지 않은 paymentKey입니다.";

        mockServer
                .expect(requestTo(url + endPoint))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body(errorMessage));
        when(responseErrorHandler.hasError(any()))
                .thenThrow(new PaymentException(UserPaymentExceptionResponse.of("INVALID_ERROR_CODE", errorMessage)));

        assertThatThrownBy(() -> tossPaymentClient.cancel(invalidPaymentRequest.paymentKey(), new CancelReason("관리자 권한 취소")))
                .isInstanceOf(PaymentException.class)
                .hasMessage(errorMessage);
    }
}
