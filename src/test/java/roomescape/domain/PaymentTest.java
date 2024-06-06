package roomescape.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.Fixture.VALID_RESERVATION;

class PaymentTest {

    @DisplayName("결제 금액과 테마 가격이 일치하지 않는 경우 예외가 발생한다.")
    @Test
    void invalidAmount() {
        Reservation reservation = VALID_RESERVATION;
        Long invalidAmount = reservation.getTheme().getPrice() + 100;

        assertThatThrownBy(() -> new Payment(reservation, "paymentKey", "orderId", invalidAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테마 가격과 결제 금액이 일치하지 않습니다.");
    }
}
