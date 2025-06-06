package roomescape.unit.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.TossApiErrorException;
import roomescape.infrastructure.payment.dto.PaymentApproveRequest;
import roomescape.infrastructure.payment.toss.TossPaymentClient;
import roomescape.infrastructure.payment.toss.dto.TossPaymentApproveErrorResponse;
import roomescape.infrastructure.payment.toss.dto.TossPaymentApproveRequest;

@RestClientTest(value = {TossPaymentClient.class})
class TossPaymentClientTest {

    @Autowired
    private TossPaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 결제_승인_요청_결과가_에러면_예외가_발생한다() throws Exception {
        // given
        TossPaymentApproveErrorResponse response = new TossPaymentApproveErrorResponse("code",
                "결제 승인에 실패했습니다.");
        PaymentApproveRequest paymentApproveRequest = new TossPaymentApproveRequest("paymentKey", "1", 1000L);
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(objectMapper.writeValueAsString(response)));
        // when
        assertThatThrownBy(() -> paymentClient.approvePayment(paymentApproveRequest))
                .isInstanceOf(TossApiErrorException.class)
                .hasMessage("결제 승인에 실패했습니다.");
    }

    @Test
    void 결제_승인_요청_결과가_성공이면_예외가_발생하지_않는다() {
        // given
        PaymentApproveRequest paymentApproveRequest = new TossPaymentApproveRequest("paymentKey", "1", 1000L);
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withStatus(HttpStatus.OK));
        // when
        assertThatCode(() -> paymentClient.approvePayment(paymentApproveRequest))
                .doesNotThrowAnyException();
    }

    @TestConfiguration
    public static class PaymentConfig {

        @Bean
        public TossPaymentClient tossPaymentClient(
                RestClient.Builder restClientBuilder,
                ObjectMapper objectMapper,
                @Value("${payment.secret-key}") String secretKey
        ) {
            RestClient restClient = restClientBuilder
                    .baseUrl("https://api.tosspayments.com/")
                    .build();
            return new TossPaymentClient(restClient, objectMapper, secretKey);
        }
    }
}
