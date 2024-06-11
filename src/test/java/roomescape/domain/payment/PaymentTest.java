package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @DisplayName("결제를 취소한다.")
    @Test
    void refund() {
        // given
        Payment payment = new Payment(
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000,
                PaymentStatus.DONE
        );

        // when
        payment.refund();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
    }
}
