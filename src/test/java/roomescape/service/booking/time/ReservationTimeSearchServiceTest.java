package roomescape.service.booking.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.time.ReservationTimeResponse;
import roomescape.dto.time.TimeWithAvailableResponse;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.time.ReservationTimeSearchService;

@Sql("/test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationTimeSearchServiceTest {

    @Autowired
    ReservationTimeSearchService reservationTimeSearchService;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Test
    void 단일_시간_조회() {
        // when
        ReservationTimeResponse timeResponse = reservationTimeSearchService.findTime(1L);

        // then
        assertAll(
                () -> assertThat(timeResponse.id()).isEqualTo(1L),
                () -> assertThat(timeResponse.startAt()).isEqualTo("10:00")
        );
    }

    @Test
    void 전체_테마_조회() {
        // when
        List<ReservationTimeResponse> allTimeResponses = reservationTimeSearchService.findAllTimes();

        // then
        assertThat(allTimeResponses).hasSize(3);
    }

    @Test
    void 특정_날짜와_테마에_예약가능_여부가_포함된_시간대를_조회() {
        // when
        List<TimeWithAvailableResponse> timesWithAvailable = reservationTimeSearchService.findAvailableTimes(
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

    @Test
    void 존재하지_않는_id로_조회할_경우_예외_발생() {
        // given
        Long notExistIdToFind = reservationTimeSearchService.findAllTimes().size() + 1L;

        // when, then
        assertThatThrownBy(() -> reservationTimeSearchService.findTime(notExistIdToFind))
                .isInstanceOf(RoomEscapeException.class);
    }
}
