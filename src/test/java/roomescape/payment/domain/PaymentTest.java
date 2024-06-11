package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("결제 도메인 테스트")
class PaymentTest {

    @DisplayName("결제를 생성한다.")
    @Test
    void create() {
        String paymentKey = "paymentKey";
        PaymentType paymentType = PaymentType.CARD;
        PayAmount payAmount = PayAmount.from(10000L);
        Payment payment = new Payment(paymentKey, paymentType, payAmount,1l);

        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getPaymentType()).isEqualTo(paymentType);
        assertThat(payment.getAmount()).isEqualTo(payAmount);
        assertThat(payment.getRelatedId()).isOne();
    }
}
