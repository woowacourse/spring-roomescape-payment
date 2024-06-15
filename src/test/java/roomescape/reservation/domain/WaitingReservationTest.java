package roomescape.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.ViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

class WaitingReservationTest {
    @Test
    @DisplayName("예약 대기 순서를 포함한 상태 이름을 반환한다.")
    void calculateOrder() {
        // given
        WaitingReservation waitingReservation = new WaitingReservation(
                MIA_RESERVATION(new ReservationTime(MIA_RESERVATION_TIME), WOOTECO_THEME(), USER_MIA(), WAITING), 1);

        // when
        String result = waitingReservation.getStatusDescription();

        // then
        assertThat(result).isEqualTo("2번째 예약대기");
    }

    @Test
    @DisplayName("예약 대기가 아닌 예약으로 새로운 객체를 생성할 수 없다.")
    void validateStatus() {
        // given
        Reservation bookingReservation = MIA_RESERVATION(new ReservationTime(MIA_RESERVATION_TIME), WOOTECO_THEME(), USER_MIA(), BOOKING);

        // when & then
        assertThatThrownBy(() -> new WaitingReservation(bookingReservation, 1))
                .isInstanceOf(ViolationException.class);
    }
}
