package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.ORDER_ID;
import static roomescape.util.Fixture.PAYMENT_KEY;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.TODAY;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

class PaymentTest {

    @DisplayName("결제 가능한 범위를 벗어나면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "2147483648"})
    void validateTotalAmountRange(String amount) {
        Reservation reservation = new Reservation(KAKI, TODAY, HORROR_THEME, RESERVATION_HOUR_10, ReservationStatus.SUCCESS);

        assertThatThrownBy(() ->
                new Payment(
                        reservation,
                        PAYMENT_KEY,
                        ORDER_ID,
                        PaymentStatus.PAID,
                        PaymentMethod.EASY_PAY,
                        PaymentCurrency.KRW,
                        new BigDecimal(amount)
                )
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
