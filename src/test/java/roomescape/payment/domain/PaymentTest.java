package roomescape.payment.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentTest {

    @Test
    void paymentKey가_null이면_예외가_발생한다() {
        // given
        final String paymentKey = null;
        final String orderId = "order123";
        final Long amount = 10000L;

        // when & then
        Assertions.assertThatThrownBy(() -> Payment.of(paymentKey, orderId, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "      "})
    void paymentKey가_blank면_예외가_발생한다(final String paymentKey) {
        // given
        final String orderId = "order123";
        final Long amount = 10000L;

        // when & then
        Assertions.assertThatThrownBy(() -> Payment.of(paymentKey, orderId, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void orderId가_null이면_예외가_발생한다() {
        // given
        final String paymentKey = "payment123";
        final String orderId = null;
        final Long amount = 10000L;

        // when & then
        Assertions.assertThatThrownBy(() -> Payment.of(paymentKey, orderId, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "      "})
    void orderId가_blank면_예외가_발생한다(final String orderId) {
        // given
        final String paymentKey = "payment123";
        final Long amount = 10000L;

        // when & then
        Assertions.assertThatThrownBy(() -> Payment.of(paymentKey, orderId, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_금액이_null이면_예외가_발생한다() {
        // given
        final String paymentKey = "payment123";
        final String orderId = "order123";
        final Long amount = null;

        // when & then
        Assertions.assertThatThrownBy(() -> Payment.of(paymentKey, orderId, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_금액이_음수면_예외가_발생한다() {
        // given
        final String paymentKey = "payment123";
        final String orderId = "order123";
        final Long amount = -1000L;

        // when & then
        Assertions.assertThatThrownBy(() -> Payment.of(paymentKey, orderId, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
