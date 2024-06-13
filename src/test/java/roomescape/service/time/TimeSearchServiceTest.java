package roomescape.service.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.time.ReservationTimeResponse;
import roomescape.dto.time.TimeWithAvailableResponse;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.ServiceBaseTest;

class TimeSearchServiceTest extends ServiceBaseTest {

    @Autowired
    TimeSearchService timeSearchService;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Test
    void 단일_시간_조회() {
        // when
        ReservationTimeResponse timeResponse = timeSearchService.findTime(1L);

        // then
        assertAll(
                () -> assertThat(timeResponse.id()).isEqualTo(1L),
                () -> assertThat(timeResponse.startAt()).isEqualTo("10:00")
        );
    }

    @Test
    void 전체_테마_조회() {
        // when
        List<ReservationTimeResponse> allTimeResponses = timeSearchService.findAllTimes();

        // then
        assertThat(allTimeResponses).hasSize(3);
    }

    @Test
    void 특정_날짜와_테마에_예약가능_여부가_포함된_시간대를_조회() {
        // when
        List<TimeWithAvailableResponse> timesWithAvailable = timeSearchService.findAvailableTimes(
                LocalDate.now().plusDays(1), 1L);

        // then
        List<ReservationTime> allTimes = timeRepository.findAll();

        assertAll(
                () -> assertThat(timesWithAvailable).hasSize(allTimes.size()),
                () -> Objects.requireNonNull(timesWithAvailable).forEach(time -> {
                    if (time.id() == 1L || time.id() == 2L) {
                        assertThat(time.alreadyBooked()).isTrue();
                    } else {
                        assertThat(time.alreadyBooked()).isFalse();
                    }
                })
        );
    }
}
