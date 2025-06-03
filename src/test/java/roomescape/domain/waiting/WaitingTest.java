package roomescape.domain.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRole;
import roomescape.exception.BusinessRuleViolationException;

class WaitingTest {

    @Test
    @DisplayName("과거 날짜로 대기를 시도하면 예외를 던진다.")
    void validateDateTime_WhenPastDate() {
        // given
        var user = User.ofExisting(2L, "사용자1", UserRole.USER, "user1@email.com", "password1");
        var timeSlot = TimeSlot.ofExisting(1L, LocalTime.of(10, 0));
        var theme = Theme.ofExisting(1L, "테마", "설명", "thumbnail");
        var pastDate = LocalDate.now().minusDays(1);

        // when & then
        assertThatThrownBy(() -> Waiting.register(user, pastDate, timeSlot, theme))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("이전 날짜로 예약 대기 신청할 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("사용자가 null인 경우 예외를 던진다.")
    void validateUser_WhenNull(User user) {
        // given
        var date = createDate();
        var timeSlot = createTimeSlot();
        var theme = createTheme();

        // when & then
        assertThatThrownBy(() -> Waiting.register(user, date, timeSlot, theme))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("사용자 정보는 null일 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("날짜가 null인 경우 예외를 던진다.")
    void validateDate_WhenNull(LocalDate date) {
        // given
        var user = createUser();
        var timeSlot = createTimeSlot();
        var theme = createTheme();

        // when & then
        assertThatThrownBy(() -> Waiting.register(user, date, timeSlot, theme))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("예약 날짜는 null일 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("타임슬롯이 null인 경우 예외를 던진다.")
    void validateTimeSlot_WhenNull(TimeSlot timeSlot) {
        // given
        var user = createUser();
        var date = createDate();
        var theme = createTheme();

        // when & then
        assertThatThrownBy(() -> Waiting.register(user, date, timeSlot, theme))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("시간 정보는 null일 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("테마가 null인 경우 예외를 던진다.")
    void validateTheme_WhenNull(Theme theme) {
        // given
        var user = createUser();
        var date = createDate();
        var timeSlot = createTimeSlot();

        // when & then
        assertThatThrownBy(() -> Waiting.register(user, date, timeSlot, theme))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("테마 정보는 null일 수 없습니다.");
    }

    @Test
    @DisplayName("예약을 정상적으로 생성한다.")
    void register_WithValidFields() {
        // given
        var user = createUser();
        var date = createDate();
        var timeSlot = createTimeSlot();
        var theme = createTheme();

        // when
        Waiting waiting = Waiting.register(user, date, timeSlot, theme);

        // then
        assertAll(
                () -> assertThat(waiting).isNotNull(),
                () -> assertThat(waiting.getUser()).isEqualTo(user),
                () -> assertThat(waiting.getDate()).isEqualTo(date),
                () -> assertThat(waiting.getTimeSlot()).isEqualTo(timeSlot),
                () -> assertThat(waiting.getTheme()).isEqualTo(theme)
        );
    }

    private static User createUser() {
        return User.ofExisting(1L, "name", UserRole.USER, "user@email.com", "password");
    }

    private static LocalDate createDate() {
        return LocalDate.now().plusDays(1);
    }

    private static TimeSlot createTimeSlot() {
        return TimeSlot.ofExisting(1L, LocalTime.of(10, 0));
    }

    private static Theme createTheme() {
        return Theme.ofExisting(1L, "테마", "설명", "thumbnail");
    }
}
