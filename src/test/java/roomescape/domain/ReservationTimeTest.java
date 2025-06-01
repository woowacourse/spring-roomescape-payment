package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReservationTimeTest {

    @Nested
    @DisplayName("예약 시간을 생성할 때 검증을 수행한다.")
    class validate {

        @Test
        @DisplayName("비어있는 시작시간으로는 예약 시간을 생성할 수 없다")
        void cannotCreateBecauseNullStartAt() {
            // when & then
            assertThatThrownBy(() -> ReservationTime.createWithoutId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비어있는 시작시간으로 예약 시간을 생성할 수 없습니다.");
        }
    }
}
