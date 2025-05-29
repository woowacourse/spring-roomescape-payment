package roomescape.payment.toss.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import roomescape.payment.toss.config.TestTossPaymentConfig;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.toss.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.toss.interceptor.TossPaymentResponseInterceptor;


@RestClientTest(TossPaymentClient.class)
@Import({TestTossPaymentConfig.class, TossPaymentResponseInterceptor.class})
class TossPaymentClientTest extends TossPaymentMockSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 결제_승인을_할_수_있다() throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(orderId);
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withSuccess(objectMapper.writeValueAsString(tossPaymentResponse), MediaType.APPLICATION_JSON));

        TossPaymentResponse result = tossPaymentClient.getPaymentConfirm(request);

        Assertions.assertThat(result.orderId()).isEqualTo(orderId);
    }

    @Test
    void 보내야하는_값이_없다면_예외를_반환한다() throws JsonProcessingException {
        String paymentKey = "";
        String orderId = "";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        String expectedMessage = "결제 키와 주문 ID는 필수입니다.";

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"결제 키와 주문 ID는 필수입니다.\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentProcessException.class)
                .hasMessage(expectedMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INVALID_API_KEY",
            "NOT_FOUND_TERMINAL_ID",
            "INVALID_AUTHORIZE_AUTH",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT",
    })
    void 서버_오류가_발생하면_예외를_반환한다(String code) throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"code\":\"" + code + "\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentServerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PROVIDER_ERROR",
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            "FAILED_INTERNAL_SYSTEM_PROCESSING"
    })
    void 일시적인_오류가_발생하면_예외를_반환한다(String code) throws JsonProcessingException {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"code\":\"" + code + "\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentTemporaryException.class);
    }
}
