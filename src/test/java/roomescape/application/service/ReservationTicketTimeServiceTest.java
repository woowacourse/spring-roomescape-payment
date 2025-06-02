package roomescape.application.service;

import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.request.ReservationTimeRegisterDto;
import roomescape.dto.response.ReservationTimeResponseDto;
import roomescape.infrastructure.db.ReservationTimeJpaRepository;
import roomescape.model.ReservationTime;

class ReservationTicketTimeServiceTest extends ServiceTest {

    @Autowired
    ReservationTimeService reservationTimeService;

    @Autowired
    ReservationTimeJpaRepository reservationTimeJpaRepository;

    @Test
    @DisplayName("시간을 저장한다")
    void test1() {
        // given
        ReservationTimeRegisterDto request = new ReservationTimeRegisterDto(LocalTime.of(15, 0).toString());

        // when
        ReservationTimeResponseDto response = reservationTimeService.saveTime(request);

        // then
        assertAll(
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.startAt()).isEqualTo(LocalTime.of(15, 0))
        );
    }

    @Test
    @DisplayName("모든 시간을 조회한다")
    void test2() {
        // when
        saveTime(LocalTime.of(10, 0));
        saveTime(LocalTime.of(14, 0));
        saveTime(LocalTime.of(18, 0));

        List<ReservationTimeResponseDto> times = reservationTimeService.getAllTimes();

        // then
        assertAll(
                () -> assertThat(times).hasSize(3),
                () -> assertThat(times).extracting("startAt")
                        .containsExactlyInAnyOrder(
                                LocalTime.of(10, 0),
                                LocalTime.of(14, 0),
                                LocalTime.of(18, 0))
        );
    }

    @Test
    @DisplayName("시간을 삭제한다")
    void test3() {
        // given
        ReservationTime reservationTime = saveTime(LocalTime.of(15, 0));

        // when
        reservationTimeService.deleteTime(reservationTime.getId());

        // then
        List<LocalTime> times = reservationTimeService.getAllTimes().stream()
                .map(ReservationTimeResponseDto::startAt)
                .toList();

        assertThat(times).doesNotContain(LocalTime.of(15, 0));
    }

    private ReservationTime saveTime(LocalTime reservationTime) {
        ReservationTime time = new ReservationTime(reservationTime);
        return reservationTimeJpaRepository.save(time);
    }

}
