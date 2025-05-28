package roomescape.infrastructure.payment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestClient;
import roomescape.business.dto.PaymentApproveDto;
import roomescape.exception.PaymentApproveException;

class TossPaymentClientTest {

    private static final String testKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private TossPaymentClient paymentClient;

    public TossPaymentClientTest() {
        this.paymentClient = new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .build(),
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(),
                testKey
        );
    }

    @Test
    void 결제시간이_만료될_경우_예외가_발생한다() {
        // given
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "1", 1000L);
        // when
        Assertions.assertThatThrownBy(() -> paymentClient.approvePayment(paymentApproveDto))
                .isInstanceOf(PaymentApproveException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }

    @Test
    void 잘못된_키로_요청을_보내면_예외가_발생한다() {
        // given
        paymentClient = new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .build(),
                new Jackson2ObjectMapperBuilder().createXmlMapper(false).build(),
                "invalidKey"
        );
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "1", 1000L);
        // when
        Assertions.assertThatThrownBy(() -> paymentClient.approvePayment(paymentApproveDto))
                .isInstanceOf(PaymentApproveException.class)
                .hasMessage("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.");
    }
}
