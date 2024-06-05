package roomescape.service.booking.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.time.ReservationTime;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.ServiceBaseTest;
import roomescape.service.booking.time.module.TimeDeleteService;

class TimeDeleteServiceTest extends ServiceBaseTest {

    @Autowired
    TimeDeleteService timeDeleteService;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Sql("/reset-data.sql")
    @Test
    void 테마_삭제() {
        // given
        timeRepository.save(new ReservationTime(LocalTime.now()));
        timeRepository.save(new ReservationTime(LocalTime.now().plusHours(1)));

        // when
        timeDeleteService.deleteTime(1L);

        // when
        List<ReservationTime> allTimes = timeRepository.findAll();
        assertThat(allTimes).extracting(ReservationTime::getId)
                .isNotEmpty()
                .doesNotContain(1L);
    }

    @Test
    void 예약이_존재하는_시간대를_삭제할_경우_예외_발생() {
        // then
        assertThatThrownBy(() -> timeDeleteService.deleteTime(1L))
                .isInstanceOf(RoomEscapeException.class);
    }
}
