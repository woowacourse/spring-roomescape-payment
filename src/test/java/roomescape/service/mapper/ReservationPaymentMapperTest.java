package roomescape.service.mapper;

import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT;
import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

class ReservationPaymentMapperTest {

    @Test
    @DisplayName("예약과 그 예약의 결제를 잘 매핑하는지 확인")
    void toMap() {
        Reservation reservation2 = new Reservation(2L, DEFAULT_RESERVATION);
        Map<Reservation, Payment> reservationPaymentMap = ReservationPaymentMapper.toMap(
                List.of(DEFAULT_RESERVATION, reservation2), List.of(DEFAULT_PAYMENT));

        Map<Reservation, Payment> expected = new HashMap<>();
        expected.put(DEFAULT_RESERVATION, DEFAULT_PAYMENT);
        expected.put(reservation2, null);

        Assertions.assertThat(reservationPaymentMap)
                .containsExactlyInAnyOrderEntriesOf(expected);
    }
}
