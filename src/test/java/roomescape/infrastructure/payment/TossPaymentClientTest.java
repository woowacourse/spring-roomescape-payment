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
import roomescape.business.dto.PaymentApproveDto;
import roomescape.exception.PaymentApproveException;

@RestClientTest(value = {ClientConfig.class, TossPaymentClient.class})
class TossPaymentClientTest {

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @Autowired
    private MockRestServiceServer mockServer;
    @Value("${payment.secret-key}")
    private String secretKey;

    // 올바른 클라이언트 연결 테스트
    @Test
    void 정상_결제_승인_요청() {
        // given
        String approveUrl = "https://api.tosspayments.com/v1/payments/confirm";
        mockServer.expect(requestTo(approveUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        Assertions.assertThatCode(
                        () -> tossPaymentClient.approvePayment(new PaymentApproveDto("paymentKey", "orderId", 1000L)))
                .doesNotThrowAnyException();
    }

    @Test
    void 결제시간이_만료될_경우_예외가_발생한다() {
        // given
        tossPaymentClient = new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com").build()
                ,
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(), secretKey
        );
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "1", 1000L);
        // when & then
        Assertions.assertThatThrownBy(() -> tossPaymentClient.approvePayment(paymentApproveDto))
                .isInstanceOf(PaymentApproveException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }

    @Test
    void 잘못된_키로_요청을_보내면_예외가_발생한다() {
        // given
        tossPaymentClient = new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com").build()
                ,
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(),
                "invalid" + secretKey
        );
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "1", 1000L);
        // when
        Assertions.assertThatThrownBy(() -> tossPaymentClient.approvePayment(paymentApproveDto))
                .isInstanceOf(PaymentApproveException.class)
                .hasMessage("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.");
    }

    // 연결 시간 예외 테스트
    // 응답 시간 예외 테스트

}
