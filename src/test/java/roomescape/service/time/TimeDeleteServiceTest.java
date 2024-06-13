package roomescape.service.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.time.ReservationTime;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.ServiceBaseTest;

class TimeDeleteServiceTest extends ServiceBaseTest {

    @Autowired
    TimeDeleteService timeDeleteService;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Sql("/reset-data.sql")
    @Test
    void 시간_삭제() {
        // given
        timeRepository.save(new ReservationTime(LocalTime.now()));
        timeRepository.save(new ReservationTime(LocalTime.now().plusHours(1)));

        // when
        timeDeleteService.deleteTime(1L);

        // then
        List<ReservationTime> allTimes = timeRepository.findAll();
        assertThat(allTimes).extracting(ReservationTime::getId)
                .isNotEmpty()
                .doesNotContain(1L);
    }
}
