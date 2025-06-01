package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import roomescape.reservation.time.domain.ReservationTime;

class ReservationTimeTest {

    @DisplayName("시작 시간이 존재하지 않으면 예약 시간을 생성할 수 없다.")
    @Test
    void createReservationTimeWithNull() {
        assertThatThrownBy(() -> new ReservationTime(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
