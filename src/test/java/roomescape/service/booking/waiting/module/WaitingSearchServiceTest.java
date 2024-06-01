package roomescape.service.booking.waiting.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.service.ServiceBaseTest;

class WaitingSearchServiceTest extends ServiceBaseTest {

    @Autowired
    WaitingSearchService waitingSearchService;

    @Test
    void 모든_예약_대기_조회() {
        // when
        List<WaitingResponse> allWaitingReservations = waitingSearchService.findAllWaitingReservations();

        // then
        assertThat(allWaitingReservations).hasSize(2);
    }
}
