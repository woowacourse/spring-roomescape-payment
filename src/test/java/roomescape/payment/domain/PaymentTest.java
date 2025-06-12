package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.exception.InvalidInputException;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("결제 객체가 정상적으로 생성된다.")
    void createPayment() {
        // given
        final String paymentKey = "paymentKey123";
        final String orderId = "orderId123";
        final Long amount = 10000L;
        final String requestedAt = "2023-10-01T10:00:00Z";
        final String approvedAt = "2023-10-01T10:05:00Z";

        // when & then
        assertThatNoException()
                .isThrownBy(() -> Payment.withoutId(paymentKey, orderId, amount, requestedAt, approvedAt));
    }

    @Test
    @DisplayName("결제 객체 필드 중 필수 값이 null이라면 예외가 발생한다.")
    void createPaymentWithNoPaymentKey() {
        // given
        final String orderId = "orderId123";
        final Long amount = 10000L;
        final String requestedAt = "2023-10-01T10:00:00Z";
        final String approvedAt = "2023-10-01T10:05:00Z";

        // when & then
        assertThatThrownBy(() -> Payment.withoutId(null, orderId, amount, requestedAt, approvedAt))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Payment.paymentKey 은(는) null일 수 없습니다.");
    }
}
