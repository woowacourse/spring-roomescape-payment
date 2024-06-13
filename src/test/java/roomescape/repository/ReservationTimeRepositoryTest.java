package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.time.ReservationTime;
import roomescape.exception.RoomEscapeException;

@Sql("/test-data.sql")
class ReservationTimeRepositoryTest extends RepositoryBaseTest {

    @Autowired
    ReservationTimeRepository timeRepository;

    @Test
    void 주어진_id에_해당하지_않는_예약시간을_조회() {
        // when
        List<ReservationTime> timeByIdNotIn = timeRepository.findByIdNotIn(List.of(1L, 2L));

        // then
        assertThat(timeByIdNotIn).extracting(ReservationTime::getId).containsOnly(3L);
    }

    @Test
    void 주어진_시간으로_등록된_예약_시간이_있는지_확인() {
        // when
        boolean result = timeRepository.existsByStartAt(LocalTime.parse("10:00"));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 존재하지_않는_id로_조회시_예외_발생() {
        // when, then
        assertThatThrownBy(() -> timeRepository.findByIdOrThrow(1000L))
                .isInstanceOf(RoomEscapeException.class);
    }
}
