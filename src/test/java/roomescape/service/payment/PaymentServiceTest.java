package roomescape.service.payment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.infrastructure.FakePaymentClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PaymentServiceTest {

    private final PaymentService paymentService;

    @Autowired
    public PaymentServiceTest(PaymentRepository paymentRepository, FakePaymentClient fakePaymentClient) {
        this.paymentService = new PaymentService(paymentRepository, fakePaymentClient);
    }

    @DisplayName("결제를 승인한다.")
    @Test
    void approvePayment() {
        // given
        PaymentRequest request = new PaymentRequest("testKey", "testOrderId", 1000L);

        // when
        Payment payment = paymentService.approvePayment(request, null);

        //then
        Assertions.assertThat(payment.getId()).isNotZero();
    }
}
