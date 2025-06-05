package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixtures.anyUserWithNewId;

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

public class ReservationQueuesTest {

    private static final AtomicLong DUMMY_ID_GENERATOR = new AtomicLong();

    private final User user = anyUserWithNewId();
    private final LocalDate date = TestFixtures.anyDateAfterToday();
    private final Theme theme = TestFixtures.anyThemeWithNewId();
    private final TimeSlot time1 = TestFixtures.anyTimeSlotWithNewId();
    private final TimeSlot time2 = TestFixtures.anyTimeSlotWithNewId();

    @Test
    @DisplayName("대기열의 예약들과 비교해 주어진 예약의 대기 순번을 계산한다.")
    void orderOf() {
        // given
        var first = waitingOfSameDate(time1);
        var second = waitingOfSameDate(time1);
        var third = waitingOfSameDate(time1);
        var fourth = waitingOfSameDate(time1);

        var queues = new ReservationQueues(List.of(first, second, third, fourth));

        // when
        var order = queues.orderOf(fourth);

        // then
        assertThat(order).isEqualTo(4);
    }

    @Test
    @DisplayName("대기열의 예약들과 비교해 주어진 모든 예약의 대기 순번을 계산한다.")
    void orderOfAll() {
        // given
        var schedule1_first = waitingOfSameDate(time1);
        var schedule1_second = waitingOfSameDate(time1);
        var schedule1_third = waitingOfSameDate(time1);

        var schedule2_first = waitingOfSameDate(time2);
        var schedule2_second = waitingOfSameDate(time2);

        var queues = new ReservationQueues(List.of(schedule1_first, schedule1_second, schedule1_third, schedule2_first, schedule2_second));

        // when
        var reservationsToOrder = List.of(schedule1_third, schedule2_second);
        var reservationWithOrders = queues.orderOfAll(reservationsToOrder);

        // then
        assertThat(reservationWithOrders).contains(
            new ReservationWithOrder(schedule1_third, 3),
            new ReservationWithOrder(schedule2_second, 2)
        );
    }

    @Test
    @DisplayName("주어진 예약 다음 순번의 예약을 대기열에서 찾는다.")
    void findNext() {
        // given
        var first = waitingOfSameDate(time1);
        var second = waitingOfSameDate(time1);
        var third = waitingOfSameDate(time1);
        var fourth = waitingOfSameDate(time1);

        var queues = new ReservationQueues(List.of(first, second, third, fourth));

        // when & then
        assertAll(
            () -> assertThat(queues.findNext(first)).hasValue(second),
            () -> assertThat(queues.findNext(second)).hasValue(third),
            () -> assertThat(queues.findNext(third)).hasValue(fourth),
            () -> assertThat(queues.findNext(fourth)).isEmpty()
        );
    }

    @Test
    @DisplayName("주어진 예약을 대기열에서 제거한다.")
    void remove() {
        // given
        var first = waitingOfSameDate(time1);
        var second = waitingOfSameDate(time1);
        var third = waitingOfSameDate(time1);

        var queues = new ReservationQueues(List.of(first, second, third));

        // when
        queues.remove(second);

        // then
        assertAll(
            () -> assertThatThrownBy(() -> queues.orderOf(second)).isInstanceOf(IllegalArgumentException.class),
            () -> assertThat(queues.findNext(first)).hasValue(third),
            () -> assertThat(queues.findNext(third)).isEmpty()
        );
    }

    private Reservation waitingOfSameDate(final TimeSlot timeSlot) {
        return new Reservation(
            DUMMY_ID_GENERATOR.incrementAndGet(),
            user,
            RoomescapeSchedule.of(date, timeSlot, theme),
            ReservationStatus.WAITING
        );
    }
}
