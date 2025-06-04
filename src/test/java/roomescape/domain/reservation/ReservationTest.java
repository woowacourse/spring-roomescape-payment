package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import roomescape.TestFixtures;
import roomescape.domain.RoomescapeSchedule;
import roomescape.domain.payment.Payment;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.exception.BusinessRuleViolationException;

public class ReservationTest {

    private final User user1 = TestFixtures.anyUserWithNewId();
    private final User user2 = TestFixtures.anyUserWithNewId();
    private final LocalDate date = LocalDate.of(2020, 1, 1);
    private final TimeSlot timeSlot = TestFixtures.anyTimeSlotWithNewId();
    private final Theme theme = TestFixtures.anyThemeWithNewId();

    @Test
    @DisplayName("주어진 예약과 같은 방탈출 일정인 지 비교할 수 있다.")
    void sameScheduleWith() {
        var schedule = RoomescapeSchedule.of(date, timeSlot, theme);
        var reservation1 = new Reservation(1L, user1, schedule, ReservationStatus.CONFIRMED);
        var reservation2 = new Reservation(2L, user2, schedule, ReservationStatus.CONFIRMED);

        assertThat(reservation1.sameScheduleWith(reservation2)).isTrue();
    }

    @Test
    @DisplayName("예약이 확정 상태인 지 확인한다.")
    void isConfirmed() {
        var reservation = reservationOf(ReservationStatus.CONFIRMED);
        assertThat(reservation.isConfirmed()).isTrue();
    }

    @Test
    @DisplayName("예약이 대기 상태인 지 확인한다.")
    void isWaiting() {
        var reservation = reservationOf(ReservationStatus.WAITING);
        assertThat(reservation.isWaiting()).isTrue();
    }

    @Test
    @DisplayName("예약이 보류 상태인 지 확인한다.")
    void isPending() {
        var reservation = reservationOf(ReservationStatus.PENDING);
        assertThat(reservation.isPending()).isTrue();
    }

    @Test
    @DisplayName("대기 상태의 예약을 취소할 수 있다.")
    void cancel() {
        // given
        var reservation = reservationOf(ReservationStatus.WAITING);

        // when
        reservation.cancel();

        // then
        assertThat(reservation.status()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("보류 상태가 아닌 예약을 보류시킬 수 있다.")
    void pend() {
        // given
        var reservation = reservationOf(ReservationStatus.WAITING);

        // when
        reservation.pend();

        // then
        assertThat(reservation.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("보류 상태의 예약을 결제 정보와 함께 확정시킬 수 있다.")
    void confirm() {
        // given
        var reservation = reservationOf(ReservationStatus.PENDING);
        var payment = new Payment("key", 10000);

        // when
        reservation.confirm(payment);

        // then
        assertThat(reservation.status()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("보류 상태가 아닌 예약은 확정시킬 수 없다.")
    void cannotConfirmNotPending() {
        // given
        var reservation = reservationOf(ReservationStatus.WAITING);
        var payment = new Payment("key", 10000);

        // when & then
        assertThatThrownBy(() -> reservation.confirm(payment)).isInstanceOf(BusinessRuleViolationException.class);
    }

    @ParameterizedTest
    @CsvSource({"CONFIRMED", "CANCELED"})
    @DisplayName("대기 상태가 아닌 예약을 취소하려 하면 예외가 발생한다.")
    void cancelNotWaitingReservation(final ReservationStatus statusThatNotWaiting) {
        var reservation = reservationOf(statusThatNotWaiting);

        assertThatThrownBy(reservation::cancel).isInstanceOf(BusinessRuleViolationException.class);
    }

    private Reservation reservationOf(final ReservationStatus status) {
        return new Reservation(1L, user1, RoomescapeSchedule.of(date, timeSlot, theme), status);
    }
}
