package roomescape.time.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.exception.InvalidInputException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ReservationTimeTest {

    @Test
    @DisplayName("예약 시간은 null일 수 없다")
    void cannotNullTime() {
        // when & then
        assertThatThrownBy(() -> ReservationTime.withoutId(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("ReservationTime.startAt 은(는) null일 수 없습니다.");
    }
}
