package roomescape.client.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.client.payment.dto.TossPaymentConfirmRequest;
import roomescape.config.ClientConfig;
import roomescape.exception.PaymentConfirmException;
import roomescape.exception.TossPaymentExceptionResponse;
import roomescape.exception.global.GlobalExceptionCode;

@ContextConfiguration(classes = ClientConfig.class)
@RestClientTest(PaymentClient.class)
@Disabled
class PaymentClientTest {

    private static final String TOSS_PAYMENT_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer mockServer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TossPaymentConfirmRequest request;

    private String jsonRequest;

    @BeforeEach
    void setPaymentRequest() throws JsonProcessingException {
        request = new TossPaymentConfirmRequest(
                "테스트 주문 id",
                new BigDecimal("1000"),
                "테스트 결제 key"
        );

        jsonRequest = objectMapper.writeValueAsString(request);
    }

    @Test
    @DisplayName("토스 결제 성공: 올바른 요청시 결제가 성공한다.")
    void validPaymentRequestConfirm() {
        mockServer.expect(requestTo(TOSS_PAYMENT_CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + paymentClient.getEncodedSecretKey()))
                .andExpect(content().json(jsonRequest))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertDoesNotThrow(() -> paymentClient.sendPaymentConfirmToToss(request));
    }

    @Test
    @DisplayName("토스 결제 실패: 4xx 에러시 결제에 실패한다.")
    void invalidRequestNotConfirm() throws JsonProcessingException {
        String errorObject = objectMapper.writeValueAsString(new TossPaymentExceptionResponse("ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다."));

        mockServer.expect(requestTo(TOSS_PAYMENT_CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + paymentClient.getEncodedSecretKey()))
                .andExpect(content().json(jsonRequest))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body(errorObject)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentClient.sendPaymentConfirmToToss(request))
                .isExactlyInstanceOf(PaymentConfirmException.class)
                .hasMessageContaining(GlobalExceptionCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
