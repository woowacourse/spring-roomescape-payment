package roomescape.domain.pendingpayment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.fixture.WaitingFixture.CREATE_WAITING_OF;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRole;

class PendingPaymentTest {

    @Test
    @DisplayName("예약 대기 상태를 이용하여 결제 대기 상태로 생성한다.")
    void fromWaiting() {
        // given
        User user = createUser();
        LocalDate date = createDate();
        TimeSlot timeSlot = createTimeSlot();
        Theme theme = createTheme();
        Waiting waiting = CREATE_WAITING_OF(1L, user, date, timeSlot, theme);

        // when
        PendingPayment pendingPayment = PendingPayment.fromWaiting(waiting);

        // then
        assertAll(
                () -> assertThat(pendingPayment).isNotNull(),
                () -> assertThat(pendingPayment.getUser()).isEqualTo(user),
                () -> assertThat(pendingPayment.getDate()).isEqualTo(date),
                () -> assertThat(pendingPayment.getTimeSlot()).isEqualTo(timeSlot),
                () -> assertThat(pendingPayment.getTheme()).isEqualTo(theme)
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
