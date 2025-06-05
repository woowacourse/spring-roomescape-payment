package roomescape.infrastructure.payment;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.business.dto.PaymentApproveRequestDto;
import roomescape.exception.PaymentApproveException;
import roomescape.infrastructure.payment.config.ClientConfig;

@RestClientTest(value = {ClientConfig.class, TossPaymentClient.class})
class TossPaymentClientTest {
    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @Value("${payment.secret-key}")
    private String secretKey;

    @Test
    void 정상_결제_승인_요청() {
        // given
        String approveUrl = "https://api.tosspayments.com/v1/payments/confirm";
        mockServer.expect(requestTo(approveUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when & then
        Assertions.assertThatCode(
                        () -> tossPaymentClient.approvePayment(new PaymentApproveRequestDto("paymentKey", "orderId", 1000L)))
                .doesNotThrowAnyException();
    }

    @Test
    void 결제시간이_만료될_경우_예외가_발생한다() {
        // given
        tossPaymentClient = new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                ,
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(), secretKey
        );
        PaymentApproveRequestDto paymentApproveRequestDto = new PaymentApproveRequestDto("paymentKey", "1", 1000L);
        // when & then
        Assertions.assertThatThrownBy(() -> tossPaymentClient.approvePayment(paymentApproveRequestDto))
                .isInstanceOf(PaymentApproveException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }

    @Test
    void 잘못된_키로_요청을_보내면_예외가_발생한다() {
        // given
        tossPaymentClient = new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                ,
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(),
                "invalid" + secretKey
        );
        PaymentApproveRequestDto paymentApproveRequestDto = new PaymentApproveRequestDto("paymentKey", "1", 1000L);
        // when & then
        Assertions.assertThatThrownBy(() -> tossPaymentClient.approvePayment(paymentApproveRequestDto))
                .isInstanceOf(PaymentApproveException.class)
                .hasMessage("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.");
    }

}
