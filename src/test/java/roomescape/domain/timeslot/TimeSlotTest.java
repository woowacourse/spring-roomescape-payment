package roomescape.domain.timeslot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import roomescape.exception.BusinessRuleViolationException;

class TimeSlotTest {

    @Test
    @DisplayName("주어진 시간이 시작 시간보다 늦은지 확인할 수 있다.")
    void isTimeBefore() {
        // given
        var timeSlot = TimeSlot.register(LocalTime.of(10, 0));
        var earlierTime = LocalTime.of(9, 0);
        var laterTime = LocalTime.of(11, 0);

        // when & then
        assertThat(timeSlot.isTimeBefore(earlierTime)).isFalse();
        assertThat(timeSlot.isTimeBefore(laterTime)).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("시간이 null이거나 공백이면 예외를 던진다.")
    void validateStartAt_WhenNull(LocalTime startAt) {
        // when & then
        assertThatThrownBy(() -> TimeSlot.register(startAt))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("시간은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("시간을 정상적으로 생성한다.")
    void register() {
        // given
        LocalTime startAt = LocalTime.of(10, 0);

        // when
        TimeSlot time = TimeSlot.register(startAt);

        // then
        assertAll(
                () -> assertThat(time).isNotNull(),
                () -> assertThat(time.getStartAt()).isEqualTo(startAt)
        );
    }
}
