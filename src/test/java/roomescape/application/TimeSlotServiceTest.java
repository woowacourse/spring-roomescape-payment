package roomescape.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.DateUtils.tomorrow;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import roomescape.domain.RoomescapeSchedule;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.exception.InUseException;

@Import(TimeSlotService.class)
class TimeSlotServiceTest extends ServiceTest {

    @Autowired
    private TimeSlotService service;

    @Test
    @DisplayName("예약 시간을 추가한다.")
    void registerTimeSlot() {
        // given
        var startAt = LocalTime.of(11, 0);

        // when
        var timeSlot = service.register(startAt);

        // then
        var timeSlots = service.findAllTimeSlots();
        assertThat(timeSlots).contains(timeSlot);
    }

    @Test
    @DisplayName("예약 시간을 삭제한다.")
    void deleteTimeSlot() {
        // given
        var timeSlot = service.register(LocalTime.of(11, 0));

        // when
        service.removeById(timeSlot.id());

        // then
        var timeSlots = service.findAllTimeSlots();
        assertThat(timeSlots).doesNotContain(timeSlot);
    }

    @Test
    @DisplayName("예약 시간을 삭제할 때 해당 시간에 대한 예약이 존재하면 예외가 발생한다.")
    void deleteTimeSlotWithReservation() {
        // given
        var timeSlot = timeSlotThatReserved();

        // when & then
        assertThatThrownBy(() -> service.removeById(timeSlot.id()))
                .isInstanceOf(InUseException.class);
    }

    private TimeSlot timeSlotThatReserved() {
        var user = repositoryHelper.saveAnyUser();
        var theme = repositoryHelper.saveAnyTheme();
        var timeSlot = service.register(LocalTime.of(10, 0));

        var reservationForTimeSlot = new Reservation(user, RoomescapeSchedule.forReserve(tomorrow(), timeSlot, theme), ReservationStatus.CONFIRMED);
        repositoryHelper.saveReservation(reservationForTimeSlot);
        return timeSlot;
    }
}
