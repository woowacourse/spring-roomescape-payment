package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ReservationPaymentTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L})
    @DisplayName("ReservationId가 1보다 작으면 예외가 발생한다.")
    void invalidReservationId(Long input) {
        assertThatCode(() -> new ReservationPayment("orderId", input, "paymentKey", 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 정보는 예약 정보가 필수입니다.");
    }

    @Test
    @DisplayName("Amount가 음수면 예외가 발생한다.")
    void invalidAmount() {
        assertThatCode(() -> new ReservationPayment("orderId", 1L, "paymentKey", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 음수일 수 없습니다.");
    }
}
