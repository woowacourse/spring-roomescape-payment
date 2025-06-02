package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.exception.ExternalApiErrorException;
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
    void 결제시간이_만료될_경우_예외가_발생한다() throws Exception {
        // given
        TossPaymentApproveErrorResponse response = new TossPaymentApproveErrorResponse("code",
                "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
        PaymentApproveRequest paymentApproveRequest = new TossPaymentApproveRequest("paymentKey", "1", 1000L);
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(objectMapper.writeValueAsString(response)));
        // when
        assertThatThrownBy(() -> paymentClient.approvePayment(paymentApproveRequest))
                .isInstanceOf(ExternalApiErrorException.class);
    }

    @Test
    void 잘못된_키로_실제_요청을_보내면_예외가_발생한다() {
        // given
        paymentClient = new TossPaymentClient(
                new RestClientBuilderConfigurer().configure(RestClient.builder()),
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(),
                "invalidKey"
        );
        PaymentApproveRequest paymentApproveRequest = new TossPaymentApproveRequest("paymentKey", "1", 1000L);
        // when
        assertThatThrownBy(() -> paymentClient.approvePayment(paymentApproveRequest))
                .isInstanceOf(ExternalApiErrorException.class);
    }
}
