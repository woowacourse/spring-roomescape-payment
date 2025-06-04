package roomescape.payment.service;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.payment.processor.toss.TossPaymentConfirmRequest;
import roomescape.payment.processor.toss.TossPaymentConfirmResponse;
import roomescape.payment.processor.toss.TossPaymentProcessor;

@SpringBootTest(classes = {PaymentService.class, TossPaymentProcessor.class})
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private TossPaymentProcessor tossPaymentProcessor;

    @Test
    void 결제를_할_수_있다() {
        // given
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
            10000,
            "orderId",
            "paymentKey"
        );
        final TossPaymentConfirmResponse expected = new TossPaymentConfirmResponse(
            "orderId",
            "paymentKey"
        );
        when(tossPaymentProcessor.processPayment(request)).thenReturn(expected);

        // when
        final TossPaymentConfirmResponse actual = paymentService.processPayment(
            request.amount(), request.orderId(), request.paymentKey()
        );

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
