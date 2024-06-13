package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.common.exception.TossPaymentException;
import roomescape.payment.config.PaymentConfig;
import roomescape.payment.service.dto.request.PaymentConfirmRequest;
import roomescape.payment.service.dto.resonse.PaymentConfirmResponse;
import roomescape.payment.service.dto.resonse.PaymentErrorResponse;

@RestClientTest(TossPaymentClient.class)
@Import(PaymentConfig.class)
class TossPaymentClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @DisplayName("결제 승인 요청 성공 시 올바른 응답을 반환받는다.")
    @Test
    void confirm() throws JsonProcessingException {
        PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
        PaymentConfirmResponse expectedResponse = new PaymentConfirmResponse(
                "paymentKey",
                "orderId",
                1000,
                "orderName",
                "DONE",
                "2024-02-13T12:17:57+09:00",
                "2024-02-13T12:18:14+09:00"
        );
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expectedResponse), MediaType.APPLICATION_JSON));

        assertThatCode(() -> tossPaymentClient.confirmPayment(request))
                .doesNotThrowAnyException();
        mockServer.verify();
    }

    @DisplayName("결제 승인 요청 실패 시 예외가 발생한다.")
    @Test
    void confirmWithInvalidRequest() throws JsonProcessingException {
        PaymentConfirmRequest request = new PaymentConfirmRequest("paymentKey", "orderId", 1000);
        PaymentErrorResponse expectedResponse = new PaymentErrorResponse(
                "ALREADY_PROCESSED_PAYMENT",
                "이미 처리된 결제 입니다."
        );

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(request)))
                .andRespond(withBadRequest().body(objectMapper.writeValueAsString(expectedResponse)));

        assertThatThrownBy(() -> tossPaymentClient.confirmPayment(request))
                .isInstanceOf(TossPaymentException.class);
        mockServer.verify();
    }
}
