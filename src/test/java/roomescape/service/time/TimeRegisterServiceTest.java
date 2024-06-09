package roomescape.service.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.time.ReservationTimeRequest;
import roomescape.dto.time.ReservationTimeResponse;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.ServiceBaseTest;

class TimeRegisterServiceTest extends ServiceBaseTest {

    @Autowired
    TimeRegisterService timeRegisterService;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Test
    void 시간_등록() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.parse("00:00"));

        // when
        ReservationTimeResponse response = timeRegisterService.registerTime(reservationTimeRequest);

        // then
        ReservationTime time = timeRepository.findByIdOrThrow(response.id());
        assertAll(
                () -> assertThat(time.getId()).isEqualTo(response.id()),
                () -> assertThat(time.getStartAt()).isEqualTo(reservationTimeRequest.startAt())
        );
    }
}
