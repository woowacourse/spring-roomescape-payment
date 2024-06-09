package roomescape.service.booking.waiting.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.waiting.Waiting;
import roomescape.service.ServiceBaseTest;

class WaitingCancelServiceTest extends ServiceBaseTest {

    @Autowired
    WaitingCancelService waitingCancelService;

    @Autowired
    WaitingRepository waitingRepository;

    @Test
    void 예약_대기_취소후_자동으로_다음_대기번호_변경() {
        // given
        int waitingOrderBefore = waitingRepository.findById(2L).orElseThrow().getWaitingOrderValue();

        // when
        waitingCancelService.cancelWaiting(1L);

        // then
        List<Waiting> allWaiting = waitingRepository.findAll();
        Waiting waiting = waitingRepository.findById(2L).orElseThrow();

        assertAll(
                () -> assertThat(allWaiting).extracting(Waiting::getId).containsOnly(2L),
                () -> assertThat(waitingOrderBefore).isEqualTo(2),
                () -> assertThat(waiting.getWaitingOrderValue()).isEqualTo(1)
        );
    }
}
