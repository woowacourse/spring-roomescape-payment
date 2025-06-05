package roomescape.time.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ReservationTimeQueryServiceTest {

    @Autowired
    private ReservationTimeQueryService reservationTimeQueryService;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Test
    @DisplayName("예약 시간을 조회할 수 있다")
    void getReservationTime() {
        // given
        LocalTime time = LocalTime.now().plusMinutes(2);
        ReservationTime savedTime = reservationTimeRepository.save(ReservationTime.from(time));
        Long id = savedTime.getId();

        // when
        ReservationTime reservationTime = reservationTimeQueryService.get(id);

        // then
        assertThat(reservationTime.getStartAt()).isEqualTo(time);
    }

    @Test
    @DisplayName("예약 시간을 전체 조회할 수 있다")
    void getAllReservationTimes() {
        // given
        reservationTimeRepository.save(ReservationTime.from(LocalTime.now().plusMinutes(2)));
        reservationTimeRepository.save(ReservationTime.from(LocalTime.of(11, 0)));

        // when
        List<ReservationTime> times = reservationTimeQueryService.getAll();

        // then
        assertThat(times).hasSize(2);
    }
}
