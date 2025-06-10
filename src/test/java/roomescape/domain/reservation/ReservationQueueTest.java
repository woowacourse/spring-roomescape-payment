package roomescape.domain.reservation;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.DateUtils.today;
import static roomescape.DateUtils.tomorrow;
import static roomescape.TestFixtures.anyThemeWithNewId;
import static roomescape.TestFixtures.anyTimeSlotWithNewId;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.TestFixtures;
import roomescape.domain.RoomescapeSchedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;

class ReservationQueueTest {

    private static final AtomicLong DUMMY_ID_GENERATOR = new AtomicLong();

    private final LocalDate tomorrow = tomorrow();
    private final TimeSlot timeSlot = anyTimeSlotWithNewId();
    private final Theme theme = anyThemeWithNewId();
    private final RoomescapeSchedule schedule = RoomescapeSchedule.of(tomorrow, timeSlot, theme);
    private final RoomescapeSchedule otherSchedule = RoomescapeSchedule.of(today(), timeSlot, theme);

    private final User user1 = TestFixtures.anyUserWithNewId();
    private final User user2 = TestFixtures.anyUserWithNewId();
    private final User user3 = TestFixtures.anyUserWithNewId();

    @Test
    @DisplayName("주어진 예약의 대기 순번을 알 수 있다.")
    void orderOf() {
        // given
        var first = reservationOf(schedule, user1);
        var second = reservationOf(schedule, user2);
        var third = reservationOf(schedule, user3);

        var queue = new ReservationQueue(List.of(first, second, third));

        // when & then
        assertAll(
            () -> assertThat(queue.orderOf(first)).isEqualTo(1),
            () -> assertThat(queue.orderOf(second)).isEqualTo(2),
            () -> assertThat(queue.orderOf(third)).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("대기열에 존재하지 않는 예약의 순번을 조회하려하면 예외가 발생한다.")
    void orderOfNotWaiting() {
        var queue = new ReservationQueue(emptyList());
        var reservation = reservationOf(schedule, user1);

        assertThatThrownBy(
            () -> queue.orderOf(reservation)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 일정의 예약 순번을 조회하려하면 예외가 발생한다.")
    void orderOfMismatchSchedule() {
        var queue = new ReservationQueue(emptyList());
        var reservation = reservationOf(otherSchedule, user1);

        assertThatThrownBy(
            () -> queue.orderOf(reservation)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주어진 예약 다음 순번의 예약을 찾는다.")
    void findNext() {
        // given
        var first = reservationOf(schedule, user1);
        var second = reservationOf(schedule, user2);
        var third = reservationOf(schedule, user3);

        var queue = new ReservationQueue(List.of(first, second, third));

        // when & then
        assertAll(
            () -> assertThat(queue.findNext(first)).hasValue(second),
            () -> assertThat(queue.findNext(second)).hasValue(third),
            () -> assertThat(queue.findNext(third)).isEmpty()
        );
    }

    @Test
    @DisplayName("주어진 예약을 대기열에서 제거한다.")
    void remove() {
        // given
        var reservation = reservationOf(schedule, user1);

        var queue = new ReservationQueue(List.of(
            reservation,
            reservationOf(schedule, user2),
            reservationOf(schedule, user3)
        ));

        // when
        queue.remove(reservation);

        // then
        assertThatThrownBy(() -> queue.orderOf(reservation))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("대기열에서 제거하려는 예약이 첫 번째 순번이고 다음 예약이 존재하는 경우 다음 예약은 보류상태가 된다.")
    void removeFirstWhenNextReservationExists() {
        // given
        var first = reservationOf(schedule, user1, ReservationStatus.CONFIRMED);
        var second = reservationOf(schedule, user2, ReservationStatus.WAITING);
        var third = reservationOf(schedule, user3, ReservationStatus.WAITING);

        var queue = new ReservationQueue(List.of(first, second, third));

        // when
        queue.remove(first);

        // then
        assertThat(second.isPending()).isTrue();
    }

    private Reservation reservationOf(final RoomescapeSchedule schedule, final User user) {
        return new Reservation(
            DUMMY_ID_GENERATOR.incrementAndGet(),
            user,
            schedule,
            ReservationStatus.CONFIRMED
        );
    }

    private Reservation reservationOf(final RoomescapeSchedule schedule, final User user, final ReservationStatus status) {
        return new Reservation(
            DUMMY_ID_GENERATOR.incrementAndGet(),
            user,
            schedule,
            status
        );
    }
}
