package roomescape.payment.service;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.payment.domain.Payment;
import roomescape.payment.processor.toss.TossPaymentConfirmRequest;
import roomescape.payment.processor.toss.TossPaymentConfirmResponse;
import roomescape.payment.processor.toss.TossPaymentProcessor;
import roomescape.payment.repository.PaymentRepositoryInterface;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private PaymentRepositoryInterface paymentRepository;

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
        final TossPaymentConfirmResponse tossPaymentConfirmResponse = new TossPaymentConfirmResponse(
                "orderId",
                "paymentKey"
        );
        final Payment payment = new Payment(
                10000,
                tossPaymentConfirmResponse.orderId(),
                tossPaymentConfirmResponse.paymentKey()
        );
        final Payment expected = new Payment(
                1L,
                10000,
                tossPaymentConfirmResponse.orderId(),
                tossPaymentConfirmResponse.paymentKey()
        );

        when(tossPaymentProcessor.processPayment(request)).thenReturn(tossPaymentConfirmResponse);
        when(paymentRepository.save(payment)).thenReturn(expected);

        // when
        final Payment actual = paymentService.processPayment(
                request.amount(), request.orderId(), request.paymentKey()
        );

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
