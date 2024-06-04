package roomescape.service.payment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.fixture.PaymentFixture;
import roomescape.service.ServiceTest;

class PaymentServiceTest extends ServiceTest {
    @Autowired
    private PaymentService paymentService;

    @DisplayName("결제를 승인한다.")
    @Test
    void approvePayment() {
        // given
        PaymentRequest request = PaymentFixture.createPaymentRequest();

        // when
        Payment payment = paymentService.approvePayment(request);

        //then
        Assertions.assertThat(payment.getId()).isNotZero();
    }
}
