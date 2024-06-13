package roomescape.service.time.module;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.time.ReservationTime;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.ServiceBaseTest;

class TimeValidatorTest extends ServiceBaseTest {

    @Autowired
    TimeValidator timeValidator;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Test
    void 중복된_시간이_있을_경우_예외_발생() {
        // given
        ReservationTime time = timeRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> timeValidator.validateTimeDuplicate(LocalTime.parse("10:00")))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 예약이_존재하는_시간대를_삭제할_경우_예외_발생() {
        // given
        ReservationTime time = timeRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> timeValidator.validateDeletable(time))
                .isInstanceOf(RoomEscapeException.class);
    }
}
