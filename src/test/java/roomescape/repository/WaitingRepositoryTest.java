package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.waiting.Waiting;

@Sql("/test-data.sql")
class WaitingRepositoryTest extends RepositoryBaseTest{

    @Autowired
    WaitingRepository waitingRepository;

    @Test
    void 예약_id로_대기_조회() {
        // when
        Waiting waiting = waitingRepository.findByReservationId(31L).orElseThrow();

        // then
        assertThat(waiting.getReservationId().getId()).isEqualTo(31L);
    }
}
