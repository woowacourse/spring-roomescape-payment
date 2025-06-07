package roomescape.timeslot.infrastructure;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.timeslot.domain.TimeSlot;

@DataJpaTest
class TimeSlotJpaRepositoryTest {

    @Autowired
    TimeSlotJpaRepository reservationTimeJpaRepository;

    @Test
    void 모든_예약_시간을_조회_할_수_있다() {
        List<TimeSlot> times = reservationTimeJpaRepository.findAll();

        assertThat(times)
                .extracting(TimeSlot::getStartAt)
                .containsExactly(
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        LocalTime.of(12, 0));
    }
}
