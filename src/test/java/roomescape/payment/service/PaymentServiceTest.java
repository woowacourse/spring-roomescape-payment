package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.payment.dto.TossPaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentConfirmResponse;
import roomescape.payment.processor.PaymentType;
import roomescape.payment.processor.toss.TossPaymentProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
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

        when(tossPaymentProcessor.supports(PaymentType.TOSS)).thenReturn(true);
        when(tossPaymentProcessor.processPayment(any(TossPaymentConfirmRequest.class))).thenReturn(expected);

        // when
        final PaymentConfirmResponse actual = paymentService.processPayment(
                PaymentType.TOSS,
                request
        );

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
