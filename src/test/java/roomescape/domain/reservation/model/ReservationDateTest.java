package roomescape.domain.reservation.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.reservation.exception.InvalidReserveInputException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationDateTest {

    @DisplayName("예약 날짜에 Null을 입력하면 예외를 발생한다.")
    @Test
    void throwExceptionWhenReservationDateNull() {
        // When & Then
        assertThatThrownBy(() -> new ReservationDate(null))
                .isInstanceOf(InvalidReserveInputException.class)
                .hasMessage("예약 날짜는 공백을 입력할 수 없습니다.");
    }
}
